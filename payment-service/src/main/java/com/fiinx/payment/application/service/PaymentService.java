package com.fiinx.payment.application.service;

import com.fiinx.common.event.payment.PaymentProcessCommand;
import com.fiinx.payment.domain.entity.Payment;
import com.fiinx.payment.domain.entity.PaymentStatus;
import com.fiinx.payment.domain.repository.PaymentRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Senior Note: Payment Service với cơ chế Idempotency
 * 
 * - Sử dụng Idempotency key để ngăn chặn việc thanh toán trùng lặp (Duplicate payments)
 * - Tích hợp Resilience4j Retry cho việc gọi các Gateway thanh toán bên ngoài
 * - Cung cấp cơ chế Refund (Bồi hoàn) trong quy trình Saga
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final RedissonClient redissonClient;
    
    private static final String IDEMPOTENCY_PREFIX = "payment:idempotency:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    /**
     * Initiate payment (Multi-gateway support)
     */
    @Transactional
    public com.fiinx.payment.application.dto.PaymentResponse initiatePayment(com.fiinx.payment.application.dto.PaymentRequest request) {
        log.info("Initiating payment for order: {} via {}", request.getOrderId(), request.getPaymentMethod());

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        payment = paymentRepository.save(payment);

        String mockUrl = switch (request.getPaymentMethod().toUpperCase()) {
            case "VNPAY" -> "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?orderId=" + payment.getId();
            case "MOMO" -> "https://test-payment.momo.vn/pay/orderId=" + payment.getId();
            case "ZALOPAY" -> "https://sb-openapi.zalopay.vn/v2/create?orderId=" + payment.getId();
            default -> "https://fiinx.com/payment/mock?id=" + payment.getId();
        };

        return com.fiinx.payment.application.dto.PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentUrl(mockUrl)
                .status("PENDING")
                .build();
    }

    /**
     * Process gateway webhook (IPN)
     */
    @Transactional
    public void processWebhook(UUID paymentId, boolean success, String transactionId) {
        log.info("Processing webhook for payment: {}, success: {}", paymentId, success);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Payment {} already processed with status {}", paymentId, payment.getStatus());
            return;
        }

        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setTransactionId(transactionId);
        payment.setProcessedAt(Instant.now());
        
        if (!success) {
            payment.setFailureReason("Gateway reported failure");
        }

        paymentRepository.save(payment);
        log.info("Payment updated to {} after webhook", payment.getStatus());
    }
    
    /**
     * Xử lý thanh toán với cơ chế kiểm tra tính Idempotency
     */
    @Transactional
    public PaymentResult processPayment(PaymentProcessCommand command) {
        String idempotencyKey = command.getIdempotencyKey();
        
        // Kiểm tra tính Idempotency - nếu đã xử lý, trả về kết quả từ cache
        Optional<PaymentResult> cachedResult = getFromIdempotencyCache(idempotencyKey);
        if (cachedResult.isPresent()) {
            log.info("Returning cached payment result for idempotency key: {}", idempotencyKey);
            return cachedResult.get();
        }
        
        // Kiểm tra xem giao dịch thanh toán đã tồn tại trong DB chưa
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(command.getOrderId());
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            return mapToResult(payment);
        }
        
        // Tạo bản ghi thanh toán mới
        Payment payment = Payment.builder()
            .orderId(command.getOrderId())
            .customerId(command.getCustomerId())
            .amount(command.getAmount())
            .currency(command.getCurrency())
            .paymentMethod(command.getPaymentMethod())
            .status(PaymentStatus.PENDING)
            .idempotencyKey(idempotencyKey)
            .createdAt(Instant.now())
            .build();
        
        payment = paymentRepository.save(payment);
        
        // Gọi Payment Gateway bên ngoài với cơ chế Retry
        PaymentResult result = callPaymentGateway(payment);
        
        // Cập nhật trạng thái thanh toán
        payment.setStatus(result.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setTransactionId(result.getTransactionId());
        payment.setProcessedAt(Instant.now());
        if (!result.isSuccess()) {
            payment.setFailureReason(result.getFailureReason());
        }
        paymentRepository.save(payment);
        
        // Lưu kết quả vào cache để đảm bảo tính Idempotency
        saveToIdempotencyCache(idempotencyKey, result);
        
        return result;
    }
    
    /**
     * Xử lý hoàn tiền (Refund - Phương thức bồi hoàn - Compensation)
     */
    @Transactional
    public void processRefund(String paymentId, String reason) {
        Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            log.warn("Cannot refund payment {} in status {}", paymentId, payment.getStatus());
            return;
        }
        
        // Gọi Payment Gateway để thực hiện Refund
        boolean refundSuccess = callRefundGateway(payment);
        
        if (refundSuccess) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAt(Instant.now());
            payment.setRefundReason(reason);
            paymentRepository.save(payment);
            log.info("Payment refunded: {}", paymentId);
        } else {
            throw new RuntimeException("Refund failed for payment: " + paymentId);
        }
    }
    
    @Retry(name = "paymentGateway", fallbackMethod = "paymentGatewayFallback")
    private PaymentResult callPaymentGateway(Payment payment) {
        // Mô phỏng việc gọi Gateway thanh toán bên ngoài (Stripe, PayPal, v.v.)
        log.info("Calling payment gateway for order: {}", payment.getOrderId());
        
        // Trong thực tế, đoạn này sẽ gọi các API của Stripe, PayPal...
        // Mô phỏng tỉ lệ thành công 95%
        if (Math.random() > 0.05) {
            return PaymentResult.builder()
                .success(true)
                .paymentId(payment.getId().toString())
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();
        } else {
            return PaymentResult.builder()
                .success(false)
                .paymentId(payment.getId().toString())
                .failureReason("Payment declined")
                .failureCode("CARD_DECLINED")
                .retryable(false)
                .build();
        }
    }
    
    private PaymentResult paymentGatewayFallback(Payment payment, Exception e) {
        log.error("Payment gateway failed after retries: {}", e.getMessage());
        return PaymentResult.builder()
            .success(false)
            .paymentId(payment.getId() != null ? payment.getId().toString() : null)
            .failureReason("Payment gateway unavailable")
            .failureCode("GATEWAY_ERROR")
            .retryable(true)
            .build();
    }
    
    private boolean callRefundGateway(Payment payment) {
        // Mô phỏng việc gọi API Refund
        log.info("Processing refund for transaction: {}", payment.getTransactionId());
        return true;
    }
    
    // ==================== Idempotency Cache ====================
    
    private Optional<PaymentResult> getFromIdempotencyCache(String key) {
        RBucket<PaymentResult> bucket = redissonClient.getBucket(IDEMPOTENCY_PREFIX + key);
        PaymentResult result = bucket.get();
        return Optional.ofNullable(result);
    }
    
    private void saveToIdempotencyCache(String key, PaymentResult result) {
        RBucket<PaymentResult> bucket = redissonClient.getBucket(IDEMPOTENCY_PREFIX + key);
        bucket.set(result, IDEMPOTENCY_TTL);
    }
    
    private PaymentResult mapToResult(Payment payment) {
        return PaymentResult.builder()
            .success(payment.getStatus() == PaymentStatus.COMPLETED)
            .paymentId(payment.getId().toString())
            .transactionId(payment.getTransactionId())
            .failureReason(payment.getFailureReason())
            .build();
    }
    
    // ==================== Result Class ====================
    
    @Data
    @lombok.Builder
    public static class PaymentResult implements java.io.Serializable {
        private boolean success;
        private String paymentId;
        private String transactionId;
        private String failureReason;
        private String failureCode;
        private boolean retryable;
    }
}

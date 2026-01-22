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
     * Xử lý thanh toán với cơ chế kiểm tra tính Idempotency
     */
    @Transactional
    public PaymentResult processPayment(PaymentProcessCommand command) {
        String idempotencyKey = command.getIdempotencyKey();
        
        // Check idempotency - if already processed, return cached result
        Optional<PaymentResult> cachedResult = getFromIdempotencyCache(idempotencyKey);
        if (cachedResult.isPresent()) {
            log.info("Returning cached payment result for idempotency key: {}", idempotencyKey);
            return cachedResult.get();
        }
        
        // Check if payment already exists in DB
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(command.getOrderId());
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            return mapToResult(payment);
        }
        
        // Create payment record
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
        
        // Call external payment gateway with retry
        PaymentResult result = callPaymentGateway(payment);
        
        // Update payment status
        payment.setStatus(result.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setTransactionId(result.getTransactionId());
        payment.setProcessedAt(Instant.now());
        if (!result.isSuccess()) {
            payment.setFailureReason(result.getFailureReason());
        }
        paymentRepository.save(payment);
        
        // Cache result for idempotency
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
        
        // Call payment gateway for refund
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
        
        // In real implementation, this would call Stripe, PayPal, etc.
        // Simulate 95% success rate
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
        // Simulate refund call
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

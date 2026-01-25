package com.fiinx.order.infrastructure.saga;

import com.fiinx.common.config.KafkaProperties;
import com.fiinx.common.event.inventory.InventoryReserveCommand;
import com.fiinx.common.event.notification.SendNotificationCommand;
import com.fiinx.common.event.payment.PaymentProcessCommand;
import com.fiinx.common.util.CorrelationIdUtils;
import com.fiinx.order.domain.entity.Order;
import com.fiinx.order.domain.entity.OrderStatus;
import com.fiinx.order.domain.repository.OrderRepository;
import com.fiinx.order.infrastructure.kafka.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Senior Note: Saga Orchestrator Pattern (Bộ điều phối Saga).
 * 
 * So sánh Choreography vs Orchestration:
 * - Choreography: Mỗi service tự biết bước tiếp theo -> Khó debug khi hệ thống lớn.
 * - Orchestration: Có một bộ điều phối trung tâm -> Dễ theo dõi luồng (Flow) và xử lý lỗi.
 * 
 * Các bước trong Order Saga:
 * 1. Tạo Order (Local Transaction).
 * 2. Giữ hàng tồn kho (Async qua Kafka).
 * 3. Xử lý thanh toán (Async qua Kafka).
 * 4. Gửi thông báo thành công (Async qua Kafka).
 * 
 * Cơ chế bồi hoàn (Compensation) khi có lỗi:
 * - Giải phóng hàng đã giữ (Inventory Release).
 * - Hoàn tiền (Refund) nếu đã thanh toán thành công.
 * - Gửi thông báo thất bại cho người dùng.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {
    
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    
    /**
     * Bắt đầu quy trình Order Saga.
     * Được gọi ngay sau khi Order được lưu vào Database thành công.
     */
    @Async("sagaExecutor")
    @Transactional
    public void startOrderSaga(Order order) {
        String correlationId = CorrelationIdUtils.getOrGenerate();
        log.info("Starting saga for order: {} with correlationId: {}", 
            order.getOrderNumber(), correlationId);
        
        try {
            // Bước 1: Cập nhật trạng thái và gửi lệnh giữ hàng (inventory reserve command)
            order.markAsPendingInventory();
            orderRepository.save(order);
            
            // Khởi tạo lệnh giữ hàng (inventory reserve command)
            InventoryReserveCommand command = InventoryReserveCommand.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .source("order-service")
                .orderId(order.getId().toString())
                .items(order.getItems().stream()
                    .map(item -> new InventoryReserveCommand.ReservationItem(
                        item.getProductId(),
                        item.getQuantity()
                    ))
                    .collect(Collectors.toList()))
                .build();
            
            eventPublisher.publishInventoryReserveCommand(command);
            log.info("Lệnh giữ hàng đã được gửi cho đơn hàng: {}", order.getOrderNumber());
            
        } catch (Exception e) {
            log.error("Saga failed to start for order: {}", order.getOrderNumber(), e);
            handleSagaFailure(order, "SAGA_START", e.getMessage());
        }
    }
    
    /**
     * Xử lý sự kiện giữ hàng thành công - chuyển sang bước Thanh toán.
     */
    @Transactional
    public void onInventoryReserved(String orderId, String reservationId, String correlationId) {
        log.info("Inventory reserved for order: {}, reservationId: {}", orderId, reservationId);
        
        Order order = orderRepository.findById(UUID.fromString(orderId))
            .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        
        try {
            order.markAsInventoryReserved(reservationId);
            order.markAsPendingPayment();
            orderRepository.save(order);
            
            // Gửi lệnh thanh toán (payment command)
            PaymentProcessCommand command = PaymentProcessCommand.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .source("order-service")
                .orderId(orderId)
                .customerId(order.getCustomerId())
                .amount(order.getTotalAmount())
                .currency(order.getCurrency())
                .paymentMethod(order.getPaymentMethod())
                .idempotencyKey(orderId + "-payment")
                .build();
            
            eventPublisher.publishPaymentCommand(command);
            log.info("Lệnh thanh toán đã được gửi cho đơn hàng: {}", order.getOrderNumber());
            
        } catch (Exception e) {
            log.error("Failed to process inventory reserved for order: {}", orderId, e);
            compensateOrder(order, "Failed during payment initiation: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý sự kiện thanh toán thành công - hoàn tất đơn hàng.
     */
    @Transactional
    public void onPaymentProcessed(String orderId, String paymentId, String correlationId) {
        log.info("Payment processed for order: {}, paymentId: {}", orderId, paymentId);
        
        Order order = orderRepository.findById(UUID.fromString(orderId))
            .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        
        try {
            order.markAsPaymentProcessed(paymentId);
            order.markAsCompleted();
            orderRepository.save(order);
            
            // Gửi thông báo thành công (notification)
            SendNotificationCommand notification = SendNotificationCommand.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .source("order-service")
                .recipientId(order.getCustomerId())
                .recipientEmail(order.getCustomerEmail())
                .type(SendNotificationCommand.NotificationType.ORDER_CONFIRMATION)
                .templateId("order-confirmation")
                .templateData(Map.of(
                    "orderNumber", order.getOrderNumber(),
                    "totalAmount", order.getTotalAmount().toString(),
                    "currency", order.getCurrency()
                ))
                .channel(SendNotificationCommand.NotificationChannel.EMAIL)
                .build();
            
            eventPublisher.publishNotificationCommand(notification);
            log.info("Order completed: {}", order.getOrderNumber());
            
        } catch (Exception e) {
            log.error("Failed to complete order: {}", orderId, e);
            // Tại thời điểm này, việc thanh toán đã hoàn tất. Chúng ta KHÔNG nên tự động hoàn tiền (refund).
            // Ghi log lỗi và để bộ phận hỗ trợ xử lý thủ công.
            order.markAsFailed("Post-payment processing failed: " + e.getMessage());
            orderRepository.save(order);
        }
    }
    
    /**
     * Xử lý khi việc giữ hàng tồn kho thất bại.
     */
    @Transactional
    public void onInventoryFailed(String orderId, String reason, String correlationId) {
        log.warn("Inventory reservation failed for order: {}. Reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(UUID.fromString(orderId))
            .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        
        order.markAsFailed("Inventory reservation failed: " + reason);
        orderRepository.save(order);
        
        // Gửi thông báo thất bại cho người dùng
        sendFailureNotification(order, "Unable to reserve inventory", correlationId);
    }
    
    /**
     * Xử lý khi việc thanh toán thất bại.
     */
    @Transactional
    public void onPaymentFailed(String orderId, String reason, String correlationId) {
        log.warn("Payment failed for order: {}. Reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(UUID.fromString(orderId))
            .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));
        
        // Thực hiện bồi hoàn (Compensate): Giải phóng hàng tồn kho
        compensateOrder(order, "Payment failed: " + reason);
    }
    
    /**
     * Thực hiện bồi hoàn (Compensate/Rollback) quy trình Saga.
     */
    @Transactional
    public void compensateOrder(Order order, String reason) {
        log.info("Compensating order: {}. Reason: {}", order.getOrderNumber(), reason);
        String correlationId = CorrelationIdUtils.getOrGenerate();
        
        // Giải phóng hàng tồn kho nếu đã giữ hàng thành công trước đó (reserved)
        if (order.getReservationId() != null) {
            eventPublisher.publishInventoryReleaseCommand(
                order.getId().toString(), 
                order.getReservationId(),
                reason,
                correlationId
            );
        }
        
        // Hoàn tiền (refund) nếu việc thanh toán đã được xử lý thành công (processed) trước đó
        if (order.getPaymentId() != null) {
            eventPublisher.publishPaymentRefundCommand(
                order.getId().toString(),
                order.getPaymentId(),
                reason,
                correlationId
            );
        }
        
        // Cập nhật trạng thái đơn hàng sang thất bại
        order.markAsFailed(reason);
        orderRepository.save(order);
        
        // Gửi thông báo thất bại cho người dùng
        sendFailureNotification(order, reason, correlationId);
    }
    
    // ==================== Private Helpers ====================
    
    private void handleSagaFailure(Order order, String step, String error) {
        log.error("Saga step {} failed for order {}. Error: {}", step, order.getOrderNumber(), error);
        order.markAsFailed("Saga thất bại tại bước " + step + ": " + error);
        orderRepository.save(order);
    }
    
    private void sendFailureNotification(Order order, String reason, String correlationId) {
        SendNotificationCommand notification = SendNotificationCommand.builder()
            .eventId(UUID.randomUUID().toString())
            .correlationId(correlationId)
            .timestamp(Instant.now())
            .source("order-service")
            .recipientId(order.getCustomerId())
            .recipientEmail(order.getCustomerEmail())
            .type(SendNotificationCommand.NotificationType.ORDER_CANCELLED)
            .templateId("order-failed")
            .templateData(Map.of(
                "orderNumber", order.getOrderNumber(),
                "reason", reason
            ))
            .channel(SendNotificationCommand.NotificationChannel.EMAIL)
            .build();
        
        eventPublisher.publishNotificationCommand(notification);
    }
}

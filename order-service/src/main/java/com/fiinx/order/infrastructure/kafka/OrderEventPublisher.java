package com.fiinx.order.infrastructure.kafka;

import com.fiinx.common.config.KafkaProperties;
import com.fiinx.common.event.inventory.InventoryReleaseCommand;
import com.fiinx.common.event.inventory.InventoryReserveCommand;
import com.fiinx.common.event.notification.SendNotificationCommand;
import com.fiinx.common.event.order.OrderCreatedEvent;
import com.fiinx.common.event.payment.PaymentProcessCommand;
import com.fiinx.common.event.payment.PaymentRefundCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * BEST PRACTICE #20: Thành phần phát tán sự kiện (Kafka Event Publisher).
 * 
 * - Quản lý việc gửi sự kiện tập trung.
 * - Truyền Correlation ID thông qua headers để phục vụ truy vết (tracing).
 * - Xử lý lỗi chặt chẽ.
 * - Gửi sự kiện không đồng bộ (Async) với các hàm callback xử lý kết quả.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String EVENT_TYPE_HEADER = "X-Event-Type";
    
    /**
     * Phát tán sự kiện đơn hàng đã được tạo (Order Created Event).
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        publish(KafkaProperties.TOPIC_ORDER_CREATED, event.getOrderId(), event, event.getCorrelationId());
    }
    
    /**
     * Phát tán lệnh giữ hàng tồn kho (Inventory Reserve Command).
     */
    public void publishInventoryReserveCommand(InventoryReserveCommand command) {
        publish(KafkaProperties.TOPIC_INVENTORY_RESERVE, command.getOrderId(), command, command.getCorrelationId());
    }
    
    /**
     * Phát tán lệnh giải phóng hàng tồn kho (Inventory Release Command - Bước bồi hoàn).
     */
    public void publishInventoryReleaseCommand(String orderId, String reservationId, 
                                                String reason, String correlationId) {
        InventoryReleaseCommand command = InventoryReleaseCommand.builder()
            .eventId(UUID.randomUUID().toString())
            .correlationId(correlationId)
            .timestamp(Instant.now())
            .source("order-service")
            .orderId(orderId)
            .reservationId(reservationId)
            .reason(reason)
            .build();
        
        publish(KafkaProperties.TOPIC_INVENTORY_RELEASE, orderId, command, correlationId);
    }
    
    /**
     * Phát tán lệnh xử lý thanh toán (Payment Process Command).
     */
    public void publishPaymentCommand(PaymentProcessCommand command) {
        publish(KafkaProperties.TOPIC_PAYMENT_PROCESS, command.getOrderId(), command, command.getCorrelationId());
    }
    
    /**
     * Phát tán lệnh hoàn tiền (Payment Refund Command - Bước bồi hoàn).
     */
    public void publishPaymentRefundCommand(String orderId, String paymentId, 
                                            String reason, String correlationId) {
        PaymentRefundCommand command = PaymentRefundCommand.builder()
            .eventId(UUID.randomUUID().toString())
            .correlationId(correlationId)
            .timestamp(Instant.now())
            .source("order-service")
            .orderId(orderId)
            .paymentId(paymentId)
            .reason(reason)
            .build();
        
        publish(KafkaProperties.TOPIC_PAYMENT_REFUND, orderId, command, correlationId);
    }
    
    /**
     * Phát tán lệnh gửi thông báo (Send Notification Command).
     */
    public void publishNotificationCommand(SendNotificationCommand command) {
        publish(KafkaProperties.TOPIC_NOTIFICATION_SEND, command.getRecipientId(), 
            command, command.getCorrelationId());
    }
    
    // ==================== Private Helpers ====================
    
    private void publish(String topic, String key, Object event, String correlationId) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, event);
        
        // Thêm headers để phục vụ việc truy vết (tracing)
        record.headers().add(new RecordHeader(
            CORRELATION_ID_HEADER, 
            correlationId != null ? correlationId.getBytes(StandardCharsets.UTF_8) : null
        ));
        record.headers().add(new RecordHeader(
            EVENT_TYPE_HEADER,
            event.getClass().getSimpleName().getBytes(StandardCharsets.UTF_8)
        ));
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);
        
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage(), ex);
                // Trong môi trường production: Cân nhắc lưu vào bảng Outbox để retry sau này.
            } else {
                log.debug("Event published to topic {} partition {} offset {}", 
                    topic, 
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }
}

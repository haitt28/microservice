package com.fiinx.order.infrastructure.kafka;

import com.fiinx.common.config.KafkaProperties;
import com.fiinx.common.event.inventory.InventoryReservationFailedEvent;
import com.fiinx.common.event.inventory.InventoryReservedEvent;
import com.fiinx.common.event.payment.PaymentFailedEvent;
import com.fiinx.common.event.payment.PaymentProcessedEvent;
import com.fiinx.common.util.CorrelationIdUtils;
import com.fiinx.order.infrastructure.saga.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * BEST PRACTICE #21: Kafka Event Consumer
 * 
 * - Manual acknowledgment cho exactly-once semantics
 * - Correlation ID extraction from headers
 * - Error handling với DLQ
 * - Idempotent processing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    
    private final OrderSagaOrchestrator sagaOrchestrator;
    
    /**
     * Handle inventory reserved event
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_INVENTORY_RESERVED,
        groupId = "${spring.kafka.consumer.group-id:order-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryReserved(ConsumerRecord<String, InventoryReservedEvent> record,
                                     Acknowledgment acknowledgment) {
        InventoryReservedEvent event = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received InventoryReservedEvent for order: {}, correlationId: {}", 
            event.getOrderId(), correlationId);
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                sagaOrchestrator.onInventoryReserved(
                    event.getOrderId(), 
                    event.getReservationId(),
                    correlationId
                );
                acknowledgment.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process InventoryReservedEvent: {}", e.getMessage(), e);
                // Không acknowledge - tin nhắn sẽ được retry hoặc đưa vào DLQ
                throw e;
            }
        });
    }
    
    /**
     * Handle inventory reservation failed event
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_INVENTORY_FAILED,
        groupId = "${spring.kafka.consumer.group-id:order-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryFailed(ConsumerRecord<String, InventoryReservationFailedEvent> record,
                                   Acknowledgment acknowledgment) {
        InventoryReservationFailedEvent event = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received InventoryReservationFailedEvent for order: {}", event.getOrderId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                sagaOrchestrator.onInventoryFailed(
                    event.getOrderId(),
                    event.getFailureReason(),
                    correlationId
                );
                acknowledgment.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process InventoryReservationFailedEvent: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
    
    /**
     * Handle payment processed event
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_PAYMENT_PROCESSED,
        groupId = "${spring.kafka.consumer.group-id:order-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentProcessed(ConsumerRecord<String, PaymentProcessedEvent> record,
                                    Acknowledgment acknowledgment) {
        PaymentProcessedEvent event = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received PaymentProcessedEvent for order: {}, paymentId: {}", 
            event.getOrderId(), event.getPaymentId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                sagaOrchestrator.onPaymentProcessed(
                    event.getOrderId(),
                    event.getPaymentId(),
                    correlationId
                );
                acknowledgment.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process PaymentProcessedEvent: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
    
    /**
     * Handle payment failed event
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_PAYMENT_FAILED,
        groupId = "${spring.kafka.consumer.group-id:order-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentFailed(ConsumerRecord<String, PaymentFailedEvent> record,
                                 Acknowledgment acknowledgment) {
        PaymentFailedEvent event = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received PaymentFailedEvent for order: {}", event.getOrderId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                sagaOrchestrator.onPaymentFailed(
                    event.getOrderId(),
                    event.getFailureReason(),
                    correlationId
                );
                acknowledgment.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process PaymentFailedEvent: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
    
    // ==================== Helper Methods ====================
    
    private String extractCorrelationId(ConsumerRecord<String, ?> record) {
        var header = record.headers().lastHeader("X-Correlation-ID");
        if (header != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return CorrelationIdUtils.generate();
    }
}

package com.fiinx.inventory.infrastructure.kafka;

import com.fiinx.common.config.KafkaProperties;
import com.fiinx.common.event.inventory.InventoryReleaseCommand;
import com.fiinx.common.event.inventory.InventoryReservationFailedEvent;
import com.fiinx.common.event.inventory.InventoryReserveCommand;
import com.fiinx.common.event.inventory.InventoryReservedEvent;
import com.fiinx.common.util.CorrelationIdUtils;
import com.fiinx.inventory.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Inventory Event Consumer
 * 
 * Listens for inventory commands and publishes result events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {
    
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Handle inventory reservation command
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_INVENTORY_RESERVE,
        groupId = "${spring.kafka.consumer.group-id:inventory-service-group}"
    )
    public void onReserveCommand(ConsumerRecord<String, InventoryReserveCommand> record,
                                  Acknowledgment ack) {
        InventoryReserveCommand command = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received InventoryReserveCommand for order: {}", command.getOrderId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                // Try to reserve inventory
                var result = inventoryService.reserveInventory(command);
                
                if (result.isSuccess()) {
                    // Publish success event
                    InventoryReservedEvent event = InventoryReservedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .correlationId(correlationId)
                        .causationId(command.getEventId())
                        .timestamp(Instant.now())
                        .source("inventory-service")
                        .orderId(command.getOrderId())
                        .reservationId(result.getReservationId())
                        .reservedItems(result.getReservedItems())
                        .build();
                    
                    kafkaTemplate.send(KafkaProperties.TOPIC_INVENTORY_RESERVED, 
                        command.getOrderId(), event);
                    log.info("Inventory reserved for order: {}", command.getOrderId());
                } else {
                    // Publish failure event
                    InventoryReservationFailedEvent event = InventoryReservationFailedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .correlationId(correlationId)
                        .causationId(command.getEventId())
                        .timestamp(Instant.now())
                        .source("inventory-service")
                        .orderId(command.getOrderId())
                        .failureReason(result.getFailureReason())
                        .failedItems(result.getFailedItems())
                        .build();
                    
                    kafkaTemplate.send(KafkaProperties.TOPIC_INVENTORY_FAILED, 
                        command.getOrderId(), event);
                    log.warn("Inventory reservation failed for order: {}", command.getOrderId());
                }
                
                ack.acknowledge();
                
            } catch (Exception e) {
                log.error("Error processing inventory reserve command: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
    
    /**
     * Handle inventory release command (compensation)
     */
    @KafkaListener(
        topics = KafkaProperties.TOPIC_INVENTORY_RELEASE,
        groupId = "${spring.kafka.consumer.group-id:inventory-service-group}"
    )
    public void onReleaseCommand(ConsumerRecord<String, InventoryReleaseCommand> record,
                                  Acknowledgment ack) {
        InventoryReleaseCommand command = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received InventoryReleaseCommand for order: {}", command.getOrderId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                inventoryService.releaseReservation(command.getReservationId());
                log.info("Inventory released for order: {}", command.getOrderId());
                ack.acknowledge();
            } catch (Exception e) {
                log.error("Error releasing inventory: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
    
    private String extractCorrelationId(ConsumerRecord<String, ?> record) {
        var header = record.headers().lastHeader("X-Correlation-ID");
        if (header != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return CorrelationIdUtils.generate();
    }
}

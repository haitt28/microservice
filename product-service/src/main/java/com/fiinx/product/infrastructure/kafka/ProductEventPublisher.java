package com.fiinx.product.infrastructure.kafka;

import com.fiinx.common.event.product.ProductCreatedEvent;
import com.fiinx.common.event.product.ProductDeletedEvent;
import com.fiinx.common.event.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Senior Note: Product Event Publisher
 * 
 * - Publish domain events qua Kafka
 * - Asynchronous để không block business logic
 * - Other services subscribe để react (Inventory, Analytics, etc.)
 * 
 * BEST PRACTICE:
 * - Fire-and-forget pattern cho events
 * - Log errors nhưng không throw exception
 * - Use Spring @Async nếu cần
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${spring.kafka.topics.product-created}")
    private String productCreatedTopic;
    
    @Value("${spring.kafka.topics.product-updated}")
    private String productUpdatedTopic;
    
    @Value("${spring.kafka.topics.product-deleted}")
    private String productDeletedTopic;
    
    /**
     * Publish ProductCreatedEvent
     */
    public void publishProductCreated(ProductCreatedEvent event) {
        try {
            kafkaTemplate.send(productCreatedTopic, event.getProductId().toString(), event);
            log.info("Published ProductCreatedEvent: productId={}", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish ProductCreatedEvent: productId={}", event.getProductId(), e);
        }
    }
    
    /**
     * Publish ProductUpdatedEvent
     */
    public void publishProductUpdated(ProductUpdatedEvent event) {
        try {
            kafkaTemplate.send(productUpdatedTopic, event.getProductId().toString(), event);
            log.info("Published ProductUpdatedEvent: productId={}", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish ProductUpdatedEvent: productId={}", event.getProductId(), e);
        }
    }
    
    /**
     * Publish ProductDeletedEvent
     */
    public void publishProductDeleted(ProductDeletedEvent event) {
        try {
            kafkaTemplate.send(productDeletedTopic, event.getProductId().toString(), event);
            log.info("Published ProductDeletedEvent: productId={}", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish ProductDeletedEvent: productId={}", event.getProductId(), e);
        }
    }
}

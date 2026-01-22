package com.fiinx.search.infrastructure.kafka;

import com.fiinx.search.application.service.SearchService;
import com.fiinx.search.domain.model.ProductIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final SearchService searchService;

    @KafkaListener(topics = "product-created", groupId = "search-service")
    public void handleProductCreated(Map<String, Object> event) {
        log.info("Received product created event: {}", event);
        // Map event to ProductIndex (Simplified for this task)
        ProductIndex product = ProductIndex.builder()
                .id(event.get("productId").toString())
                .name((String) event.get("name"))
                .slug((String) event.get("slug"))
                .description((String) event.get("description"))
                .active(true)
                .build();
        searchService.indexProduct(product);
    }

    @KafkaListener(topics = "product-updated", groupId = "search-service")
    public void handleProductUpdated(Map<String, Object> event) {
        log.info("Received product updated event: {}", event);
        // Logic to update existing index
    }

    @KafkaListener(topics = "product-deleted", groupId = "search-service")
    public void handleProductDeleted(Map<String, Object> event) {
        log.info("Received product deleted event: {}", event);
        searchService.removeProduct(event.get("productId").toString());
    }
}

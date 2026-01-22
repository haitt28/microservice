package com.fiinx.analytics.infrastructure.kafka;

import com.fiinx.analytics.application.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "order-placed", groupId = "analytics-service")
    public void handleOrderPlaced(Map<String, Object> event) {
        log.info("Received order placed event for analytics: {}", event);
        BigDecimal totalAmount = new BigDecimal(event.get("totalAmount").toString());
        analyticsService.recordOrder(totalAmount);
    }

    @KafkaListener(topics = "customer-registered", groupId = "analytics-service")
    public void handleCustomerRegistered(Map<String, Object> event) {
        log.info("Received customer registered event for analytics: {}", event);
        analyticsService.recordCustomer();
    }
}

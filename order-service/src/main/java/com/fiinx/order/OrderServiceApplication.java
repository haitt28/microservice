package com.fiinx.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Order Service Application
 * 
 * Responsibilities:
 * - Order CRUD operations
 * - Saga orchestration for order flow
 * - Event publishing (Kafka)
 * - Distributed lock management
 */
@SpringBootApplication(scanBasePackages = {"com.fiinx.order", "com.fiinx.common"})
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

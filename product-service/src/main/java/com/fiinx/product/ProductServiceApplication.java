package com.fiinx.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Senior Note: Product Service Application
 * 
 * - @EnableJpaAuditing: Tự động cập nhật createdAt, updatedAt từ BaseEntity
 * - @EnableCaching: Kích hoạt Spring Cache với Redis
 * - @EnableKafka: Kích hoạt Kafka messaging
 * - @EnableAsync: Hỗ trợ async processing
 * 
 * BEST PRACTICE:
 * - Component scan tự động từ package root
 * - Auto-configuration từ Spring Boot
 */
@SpringBootApplication(scanBasePackages = {
    "com.fiinx.product",
    "com.fiinx.common"  // Scan common library
})
@EnableJpaAuditing
@EnableCaching
@EnableKafka
@EnableAsync
public class ProductServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

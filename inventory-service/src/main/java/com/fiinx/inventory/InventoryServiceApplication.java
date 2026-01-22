package com.fiinx.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Lớp khởi chạy Identity Service.
 * Senior Note: Tích hợp JPA Auditing để tự động theo dõi thời gian tạo và cập nhật của Entity.
 */
@SpringBootApplication(scanBasePackages = {"com.fiinx.inventory", "com.fiinx.common"})
@EnableJpaAuditing
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

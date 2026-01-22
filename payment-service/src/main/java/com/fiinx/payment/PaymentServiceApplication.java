package com.fiinx.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Lớp khởi chạy Payment Service.
 * Senior Note: Tích hợp JPA Auditing để tự động quản lý thời gian bản ghi.
 */
@SpringBootApplication(scanBasePackages = {"com.fiinx.payment", "com.fiinx.common"})
@EnableJpaAuditing
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}

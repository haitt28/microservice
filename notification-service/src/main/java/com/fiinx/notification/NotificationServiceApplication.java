package com.fiinx.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Lớp khởi chạy Notification Service.
 * Senior Note: Kích hoạt @EnableAsync để xử lý các tác vụ gửi mail/sms một cách bất đồng bộ.
 */
@SpringBootApplication(scanBasePackages = {"com.fiinx.notification", "com.fiinx.common"})
@EnableAsync
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

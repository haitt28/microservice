package com.fiinx.notification.infrastructure.kafka;

import com.fiinx.common.config.KafkaProperties;
import com.fiinx.common.event.notification.SendNotificationCommand;
import com.fiinx.common.util.CorrelationIdUtils;
import com.fiinx.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Senior Note: Notification Service - Consumer lắng nghe các sự kiện từ Kafka.
 * 
 * - Đây là hệ thống xử lý bất đối xứng (Asynchronous), giúp tách biệt logic thông báo 
 *   ra khỏi luồng xử lý chính của Order/Payment.
 * - Sử dụng @KafkaListener để tự động lắng nghe các Topic tương ứng.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {
    
    private final NotificationService notificationService;
    
    @KafkaListener(
        topics = KafkaProperties.TOPIC_NOTIFICATION_SEND,
        groupId = "${spring.kafka.consumer.group-id:notification-service-group}"
    )
    public void onSendNotification(ConsumerRecord<String, SendNotificationCommand> record,
                                    Acknowledgment ack) {
        SendNotificationCommand command = record.value();
        String correlationId = extractCorrelationId(record);
        
        log.info("Received notification command: type={}, recipient={}", 
            command.getType(), command.getRecipientId());
        
        CorrelationIdUtils.runWithCorrelationId(correlationId, () -> {
            try {
                notificationService.sendNotification(command);
                ack.acknowledge();
                log.info("Notification sent successfully");
            } catch (Exception e) {
                log.error("Failed to send notification: {}", e.getMessage(), e);
                // In production: might want to retry or send to DLQ
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

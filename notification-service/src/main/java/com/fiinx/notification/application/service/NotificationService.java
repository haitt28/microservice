package com.fiinx.notification.application.service;

import com.fiinx.common.event.notification.SendNotificationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * BEST PRACTICE #37: Notification Service với Template Support
 * 
 * - Template-based notifications
 * - Multiple channels (Email, SMS, Push)
 * - Async processing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    // In real implementation, inject EmailService, SMSService, PushService
    
    @Async("notificationExecutor")
    public void sendNotification(SendNotificationCommand command) {
        log.info("Sending {} notification to {}", command.getType(), command.getRecipientId());
        
        switch (command.getChannel()) {
            case EMAIL -> sendEmail(command);
            case SMS -> sendSms(command);
            case PUSH -> sendPush(command);
            case ALL -> {
                sendEmail(command);
                sendSms(command);
                sendPush(command);
            }
        }
    }
    
    private void sendEmail(SendNotificationCommand command) {
        if (command.getRecipientEmail() == null) {
            log.warn("No email address for recipient: {}", command.getRecipientId());
            return;
        }
        
        String subject = getEmailSubject(command.getType());
        String body = buildEmailBody(command.getTemplateId(), command.getTemplateData());
        
        // In production: use JavaMailSender
        log.info("Sending email to {}: subject={}", command.getRecipientEmail(), subject);
        log.debug("Email body: {}", body);
        
        // Simulate email sending
        simulateSend();
    }
    
    private void sendSms(SendNotificationCommand command) {
        if (command.getRecipientPhone() == null) {
            log.warn("No phone number for recipient: {}", command.getRecipientId());
            return;
        }
        
        String smsBody = buildSmsBody(command.getTemplateId(), command.getTemplateData());
        
        // In production: use Twilio, AWS SNS, etc.
        log.info("Sending SMS to {}: {}", command.getRecipientPhone(), smsBody);
        
        simulateSend();
    }
    
    private void sendPush(SendNotificationCommand command) {
        // In production: use Firebase Cloud Messaging, APNs, etc.
        log.info("Sending push notification to userId: {}", command.getRecipientId());
        
        simulateSend();
    }
    
    private String getEmailSubject(SendNotificationCommand.NotificationType type) {
        return switch (type) {
            case ORDER_CONFIRMATION -> "Order Confirmation - Your order has been placed";
            case ORDER_SHIPPED -> "Your order has been shipped";
            case ORDER_DELIVERED -> "Your order has been delivered";
            case ORDER_CANCELLED -> "Order Cancellation Notice";
            case PAYMENT_SUCCESS -> "Payment Successful";
            case PAYMENT_FAILED -> "Payment Failed - Action Required";
            case REFUND_PROCESSED -> "Refund Processed";
        };
    }
    
    private String buildEmailBody(String templateId, Map<String, Object> data) {
        // In production: use Thymeleaf, FreeMarker, etc.
        StringBuilder sb = new StringBuilder();
        sb.append("Template: ").append(templateId).append("\n\n");
        
        if (data != null) {
            data.forEach((key, value) -> 
                sb.append(key).append(": ").append(value).append("\n"));
        }
        
        return sb.toString();
    }
    
    private String buildSmsBody(String templateId, Map<String, Object> data) {
        // Short SMS format
        if (data.containsKey("orderNumber")) {
            return String.format("Order %s confirmed. Thank you for your purchase!", 
                data.get("orderNumber"));
        }
        return "Notification from FiinX";
    }
    
    private void simulateSend() {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

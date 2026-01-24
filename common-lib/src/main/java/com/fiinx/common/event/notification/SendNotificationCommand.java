package com.fiinx.common.event.notification;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Command to send notification
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SendNotificationCommand extends DomainEvent {
    
    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;
    private String title;
    private String message;
    private NotificationType type;
    private String templateId;
    private Map<String, Object> templateData;
    private NotificationChannel channel;
    
    public enum NotificationType {
        ORDER_CONFIRMATION,
        ORDER_SHIPPED,
        ORDER_DELIVERED,
        ORDER_CANCELLED,
        PAYMENT_SUCCESS,
        PAYMENT_FAILED,
        REFUND_PROCESSED
    }
    
    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        ALL
    }
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

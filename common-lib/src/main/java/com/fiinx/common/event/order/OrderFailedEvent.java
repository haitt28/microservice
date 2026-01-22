package com.fiinx.common.event.order;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event emitted when order processing fails
 * Triggers compensation in saga
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFailedEvent extends DomainEvent {
    
    private String orderId;
    private String customerId;
    private String failureReason;
    private String failedStep;  // Which saga step failed: INVENTORY, PAYMENT, etc.
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

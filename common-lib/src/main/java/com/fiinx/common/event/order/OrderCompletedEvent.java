package com.fiinx.common.event.order;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Event emitted when order processing is completed successfully
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCompletedEvent extends DomainEvent {
    
    private String orderId;
    private String customerId;
    private String paymentId;
    private String orderStatus;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

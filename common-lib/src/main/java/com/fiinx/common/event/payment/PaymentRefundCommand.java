package com.fiinx.common.event.payment;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Command to refund a payment (compensation)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentRefundCommand extends DomainEvent {
    
    private String orderId;
    private String paymentId;
    private String reason;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

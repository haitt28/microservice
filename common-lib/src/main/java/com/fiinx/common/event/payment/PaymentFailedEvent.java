package com.fiinx.common.event.payment;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event emitted when payment processing fails
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentFailedEvent extends DomainEvent {
    
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private String failureReason;
    private String failureCode;  // e.g., INSUFFICIENT_FUNDS, CARD_DECLINED
    private boolean retryable;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("payment-service");
    }
}

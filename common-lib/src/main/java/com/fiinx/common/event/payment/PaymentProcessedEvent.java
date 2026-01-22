package com.fiinx.common.event.payment;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when payment is successfully processed
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends DomainEvent {
    
    private String orderId;
    private String paymentId;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private Instant processedAt;
    private String paymentStatus;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("payment-service");
    }
}

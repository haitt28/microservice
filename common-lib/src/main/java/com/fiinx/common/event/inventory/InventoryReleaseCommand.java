package com.fiinx.common.event.inventory;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Command to release previously reserved inventory (compensation)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReleaseCommand extends DomainEvent {
    
    private String orderId;
    private String reservationId;
    private String reason;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

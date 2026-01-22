package com.fiinx.common.event.inventory;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Event emitted when inventory reservation fails
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservationFailedEvent extends DomainEvent {
    
    private String orderId;
    private String failureReason;
    private List<FailedItem> failedItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedItem {
        private String productId;
        private int requestedQuantity;
        private int availableQuantity;
    }
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("inventory-service");
    }
}

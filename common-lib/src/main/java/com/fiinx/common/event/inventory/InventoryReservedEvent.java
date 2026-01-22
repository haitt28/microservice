package com.fiinx.common.event.inventory;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Event emitted when inventory is successfully reserved
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReservedEvent extends DomainEvent {
    
    private String orderId;
    private String reservationId;
    private List<ReservedItem> reservedItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedItem {
        private String productId;
        private int quantity;
        private String warehouseId;
    }
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("inventory-service");
    }
}

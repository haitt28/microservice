package com.fiinx.common.event.inventory;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Command event to reserve inventory for an order
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryReserveCommand extends DomainEvent {
    
    private String orderId;
    private List<ReservationItem> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationItem {
        private String productId;
        private int quantity;
    }
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

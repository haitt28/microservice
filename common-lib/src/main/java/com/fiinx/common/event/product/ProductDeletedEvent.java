package com.fiinx.common.event.product;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Event emitted when a product is deleted (soft-deleted)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDeletedEvent extends DomainEvent {
    
    private UUID productId;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("product-service");
    }
}

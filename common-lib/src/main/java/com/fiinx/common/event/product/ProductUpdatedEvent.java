package com.fiinx.common.event.product;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event emitted when a product is updated
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductUpdatedEvent extends DomainEvent {
    
    private UUID productId;
    private String name;
    private String slug;
    private String sku;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private String status;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("product-service");
    }
}

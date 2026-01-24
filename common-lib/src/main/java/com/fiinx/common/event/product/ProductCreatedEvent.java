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
 * Event emitted when a product is successfully created
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductCreatedEvent extends DomainEvent {
    
    private UUID productId;
    private String name;
    private String slug;
    private String sku;
    private BigDecimal basePrice;
    private String categoryId;
    private String brandId;
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("product-service");
    }
}

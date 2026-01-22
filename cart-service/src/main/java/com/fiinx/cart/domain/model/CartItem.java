package com.fiinx.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cart Item - Item trong giỏ hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Product ID
     */
    private UUID productId;
    
    /**
     * Product name (denormalized)
     */
    private String productName;
    
    /**
     * Product SKU
     */
    private String productSku;
    
    /**
     * Product image URL
     */
    private String productImageUrl;
    
    /**
     * Variant ID (nếu có)
     */
    private UUID variantId;
    
    /**
     * Variant name (ví dụ: "Red - XL")
     */
    private String variantName;
    
    /**
     * Unit price
     */
    private BigDecimal unitPrice;
    
    /**
     * Quantity
     */
    private Integer quantity;
    
    /**
     * Total price (unitPrice * quantity)
     */
    private BigDecimal totalPrice;
    
    /**
     * Stock available (denormalized để check)
     */
    private Integer stockAvailable;
    
    /**
     * Update totals
     */
    public void updateTotals() {
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * Check if in stock
     */
    public boolean isInStock() {
        return stockAvailable != null && stockAvailable >= quantity;
    }
}

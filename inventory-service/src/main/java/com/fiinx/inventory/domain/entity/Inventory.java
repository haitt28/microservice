package com.fiinx.inventory.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Inventory/Product entity
 */
@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_sku", columnList = "sku")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {
    
    @Column(name = "product_id", unique = true, nullable = false)
    private java.util.UUID productId;
    
    @Column(name = "sku", unique = true, nullable = false, length = 100)
    private String sku;
    
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;
    
    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;
    
    @Column(name = "warehouse_id", length = 50)
    private String warehouseId;
    
    
    // ==================== Business Methods ====================
    
    /**
     * Reserve stock (pessimistic lock required)
     */
    public boolean reserve(int quantity) {
        if (availableQuantity >= quantity) {
            availableQuantity -= quantity;
            reservedQuantity += quantity;
            return true;
        }
        return false;
    }
    
    /**
     * Release reserved stock (compensation)
     */
    public void releaseReservation(int quantity) {
        reservedQuantity = Math.max(0, reservedQuantity - quantity);
        availableQuantity += quantity;
    }
    
    /**
     * Confirm reservation (deduct from reserved)
     */
    public void confirmReservation(int quantity) {
        reservedQuantity = Math.max(0, reservedQuantity - quantity);
    }
    
    /**
     * Add stock
     */
    public void addStock(int quantity) {
        availableQuantity += quantity;
    }
    
    public int getTotalQuantity() {
        return availableQuantity + reservedQuantity;
    }
}

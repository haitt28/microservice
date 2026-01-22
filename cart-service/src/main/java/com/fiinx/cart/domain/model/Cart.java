package com.fiinx.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Senior Note: Cart model - stored in Redis
 * 
 * - Không dùng JPA entity vì lưu trong Redis
 * - Serializable để Redis có thể store
 * - Session-based cho guest, user-based cho authenticated
 * - TTL 24h cho inactive carts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Cart ID (session ID hoặc user ID)
     */
    private String id;
    
    /**
     * User ID (null nếu guest)
     */
    private String userId;
    
    /**
     * Session ID (cho guest)
     */
    private String sessionId;
    
    /**
     * Danh sách items trong cart
     */
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    
    /**
     * Tổng tiền trước giảm giá
     */
    private BigDecimal subtotal;
    
    /**
     * Mã giảm giá (nếu có)
     */
    private String couponCode;
    
    /**
     * Giá trị giảm giá
     */
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;
    
    /**
     * Tổng tiền sau giảm giá
     */
    private BigDecimal total;
    
    /**
     * Thời gian tạo
     */
    private Instant createdAt;
    
    /**
     * Thời gian cập nhật cuối
     */
    private Instant updatedAt;
    
    // ==================== Business Logic ====================
    
    /**
     * Thêm item vào cart
     */
    public void addItem(CartItem item) {
        // Check if product already exists
        CartItem existingItem = findItemByProductId(item.getProductId());
        
        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            existingItem.updateTotals();
        } else {
            items.add(item);
        }
        
        recalculateTotals();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Remove item
     */
    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotals();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Update quantity
     */
    public void updateItemQuantity(UUID productId, int quantity) {
        CartItem item = findItemByProductId(productId);
        if (item != null) {
            item.setQuantity(quantity);
            item.updateTotals();
            recalculateTotals();
            this.updatedAt = Instant.now();
        }
    }
    
    /**
     * Clear cart
     */
    public void clear() {
        items.clear();
        couponCode = null;
        discount = BigDecimal.ZERO;
        recalculateTotals();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Apply coupon
     */
    public void applyCoupon(String code, BigDecimal discountAmount) {
        this.couponCode = code;
        this.discount = discountAmount;
        recalculateTotals();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Remove coupon
     */
    public void removeCoupon() {
        this.couponCode = null;
        this.discount = BigDecimal.ZERO;
        recalculateTotals();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Recalculate totals
     */
    public void recalculateTotals() {
        // Calculate subtotal
        this.subtotal = items.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total (subtotal - discount)
        this.total = subtotal.subtract(discount);
        
        // Ensure non-negative
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
    }
    
    /**
     * Find item by product ID
     */
    private CartItem findItemByProductId(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get total items count
     */
    public int getTotalItems() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

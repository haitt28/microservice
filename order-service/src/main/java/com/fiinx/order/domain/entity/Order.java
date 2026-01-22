package com.fiinx.order.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * BEST PRACTICE #14: Rich Domain Entity
 * 
 * - Encapsulate business logic trong entity
 * - Sử dụng state pattern cho order status
 * - Immutable business rules
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_customer", columnList = "customer_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;
    
    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;
    
    @Column(name = "customer_email", length = 255)
    private String customerEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(name = "shipping_amount", precision = 15, scale = 2)
    private BigDecimal shippingAmount;
    
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "VND";
    
    @Embedded
    private ShippingAddress shippingAddress;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "payment_id", length = 100)
    private String paymentId;
    
    @Column(name = "reservation_id", length = 100)
    private String reservationId;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    @Column(name = "completed_at")
    private Instant completedAt;
    
    @Column(name = "cancelled_at")
    private Instant cancelledAt;
    
    // ==================== Business Methods ====================
    
    /**
     * Add item to order
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotals();
    }
    
    /**
     * Remove item from order
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotals();
    }
    
    /**
     * Recalculate order totals
     */
    public void recalculateTotals() {
        this.subtotal = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = this.subtotal
            .add(this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO)
            .add(this.shippingAmount != null ? this.shippingAmount : BigDecimal.ZERO)
            .subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
    }
    
    // ==================== State Transitions ====================
    // BEST PRACTICE: Explicit state machine with validation
    
    public void markAsPendingInventory() {
        validateTransition(OrderStatus.PENDING_INVENTORY);
        this.status = OrderStatus.PENDING_INVENTORY;
    }
    
    public void markAsInventoryReserved(String reservationId) {
        validateTransition(OrderStatus.INVENTORY_RESERVED);
        this.status = OrderStatus.INVENTORY_RESERVED;
        this.reservationId = reservationId;
    }
    
    public void markAsPendingPayment() {
        validateTransition(OrderStatus.PENDING_PAYMENT);
        this.status = OrderStatus.PENDING_PAYMENT;
    }
    
    public void markAsPaymentProcessed(String paymentId) {
        validateTransition(OrderStatus.PAYMENT_PROCESSED);
        this.status = OrderStatus.PAYMENT_PROCESSED;
        this.paymentId = paymentId;
    }
    
    public void markAsCompleted() {
        validateTransition(OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
        this.completedAt = Instant.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = OrderStatus.FAILED;
        this.failureReason = reason;
    }
    
    public void markAsCancelled(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.failureReason = reason;
        this.cancelledAt = Instant.now();
    }
    
    private void validateTransition(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus));
        }
    }
    
    public boolean isCompensatable() {
        return this.status == OrderStatus.FAILED || 
               this.status == OrderStatus.CANCELLED;
    }
    
    // ==================== Embedded Classes ====================
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShippingAddress {
        @Column(name = "recipient_name", length = 200)
        private String recipientName;
        
        @Column(name = "phone", length = 20)
        private String phone;
        
        @Column(name = "address_line1", length = 255)
        private String addressLine1;
        
        @Column(name = "address_line2", length = 255)
        private String addressLine2;
        
        @Column(name = "city", length = 100)
        private String city;
        
        @Column(name = "state", length = 100)
        private String state;
        
        @Column(name = "postal_code", length = 20)
        private String postalCode;
        
        @Column(name = "country", length = 2)
        private String country;
    }
}

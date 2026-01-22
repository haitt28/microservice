package com.fiinx.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Inventory Reservation entity
 * Tracks reservations for saga compensation
 */
@Entity
@Table(name = "inventory_reservations", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_order_id", columnList = "order_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "reservation_id", nullable = false, length = 100)
    private String reservationId;
    
    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;
    
    @Column(name = "product_id", nullable = false, length = 100)
    private String productId;
    
    @Column(name = "quantity", nullable = false)
    private int quantity;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;
    
    @Column(name = "reserved_at")
    private Instant reservedAt;
    
    @Column(name = "released_at")
    private Instant releasedAt;
    
    @Column(name = "confirmed_at")
    private Instant confirmedAt;
    
    public enum ReservationStatus {
        RESERVED,   // Stock is reserved
        CONFIRMED,  // Order completed, stock permanently deducted
        RELEASED,   // Compensation: stock released back
        FAILED      // Reservation failed
    }
}

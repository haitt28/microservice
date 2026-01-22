package com.fiinx.inventory.application.service;

import com.fiinx.common.event.inventory.InventoryReservationFailedEvent;
import com.fiinx.common.event.inventory.InventoryReserveCommand;
import com.fiinx.common.event.inventory.InventoryReservedEvent;
import com.fiinx.inventory.domain.entity.Inventory;
import com.fiinx.inventory.domain.entity.InventoryReservation;
import com.fiinx.inventory.domain.repository.InventoryRepository;
import com.fiinx.inventory.domain.repository.InventoryReservationRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Senior Note: Inventory Service với cơ chế Pessimistic Locking
 * 
 * - Sử dụng Pessimistic Lock (Lock dòng) để ngăn chặn việc bán quá số lượng (Overselling)
 * - Triển khai Reservation pattern để phục vụ cho các bước trong Saga
 * - Cung cấp các phương thức bồi hoàn (Compensation) để rollback dữ liệu khi cần
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    
    /**
     * Giữ (Reserve) hàng tồn kho cho đơn hàng (Bước trong Saga)
     * Sử dụng Pessimistic Locking để ngăn chặn tranh chấp (Race condition)
     */
    @Transactional
    public ReservationResult reserveInventory(InventoryReserveCommand command) {
        String reservationId = UUID.randomUUID().toString();
        List<InventoryReservedEvent.ReservedItem> reservedItems = new ArrayList<>();
        List<InventoryReservationFailedEvent.FailedItem> failedItems = new ArrayList<>();
        
        // Try to reserve each item
        for (var item : command.getItems()) {
            // Pessimistic lock on inventory row
            Optional<Inventory> inventoryOpt = inventoryRepository
                .findByProductIdForUpdate(item.getProductId());
            
            if (inventoryOpt.isEmpty()) {
                failedItems.add(new InventoryReservationFailedEvent.FailedItem(
                    item.getProductId(), item.getQuantity(), 0
                ));
                continue;
            }
            
            Inventory inventory = inventoryOpt.get();
            
            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                failedItems.add(new InventoryReservationFailedEvent.FailedItem(
                    item.getProductId(), 
                    item.getQuantity(), 
                    inventory.getAvailableQuantity()
                ));
            } else {
                // Reserve stock
                inventory.reserve(item.getQuantity());
                inventoryRepository.save(inventory);
                
                // Record reservation for potential rollback
                InventoryReservation reservation = InventoryReservation.builder()
                    .reservationId(reservationId)
                    .orderId(command.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .status(InventoryReservation.ReservationStatus.RESERVED)
                    .reservedAt(Instant.now())
                    .build();
                reservationRepository.save(reservation);
                
                reservedItems.add(new InventoryReservedEvent.ReservedItem(
                    item.getProductId(),
                    item.getQuantity(),
                    inventory.getWarehouseId()
                ));
            }
        }
        
        // If any items failed, rollback all reservations
        if (!failedItems.isEmpty()) {
            rollbackReservation(reservationId);
            return ReservationResult.failure(
                "Insufficient inventory for some items",
                failedItems
            );
        }
        
        return ReservationResult.success(reservationId, reservedItems);
    }
    
    /**
     * Giải phóng (Release) hàng đã giữ (Phương thức bồi hoàn - Compensation)
     */
    @Transactional
    public void releaseReservation(String reservationId) {
        List<InventoryReservation> reservations = reservationRepository
            .findByReservationId(reservationId);
        
        for (InventoryReservation reservation : reservations) {
            if (reservation.getStatus() == InventoryReservation.ReservationStatus.RESERVED) {
                Inventory inventory = inventoryRepository
                    .findByProductIdForUpdate(reservation.getProductId())
                    .orElse(null);
                
                if (inventory != null) {
                    inventory.releaseReservation(reservation.getQuantity());
                    inventoryRepository.save(inventory);
                }
                
                reservation.setStatus(InventoryReservation.ReservationStatus.RELEASED);
                reservation.setReleasedAt(Instant.now());
                reservationRepository.save(reservation);
            }
        }
        
        log.info("Released reservation: {}", reservationId);
    }
    
    /**
     * Xác nhận (Confirm) việc giữ hàng (Sau khi thanh toán thành công)
     */
    @Transactional
    public void confirmReservation(String reservationId) {
        List<InventoryReservation> reservations = reservationRepository
            .findByReservationId(reservationId);
        
        for (InventoryReservation reservation : reservations) {
            if (reservation.getStatus() == InventoryReservation.ReservationStatus.RESERVED) {
                Inventory inventory = inventoryRepository
                    .findByProductIdForUpdate(reservation.getProductId())
                    .orElse(null);
                
                if (inventory != null) {
                    inventory.confirmReservation(reservation.getQuantity());
                    inventoryRepository.save(inventory);
                }
                
                reservation.setStatus(InventoryReservation.ReservationStatus.CONFIRMED);
                reservation.setConfirmedAt(Instant.now());
                reservationRepository.save(reservation);
            }
        }
        
        log.info("Confirmed reservation: {}", reservationId);
    }
    
    private void rollbackReservation(String reservationId) {
        List<InventoryReservation> reservations = reservationRepository
            .findByReservationId(reservationId);
        
        for (InventoryReservation reservation : reservations) {
            Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(reservation.getProductId())
                .orElse(null);
            
            if (inventory != null && 
                reservation.getStatus() == InventoryReservation.ReservationStatus.RESERVED) {
                inventory.releaseReservation(reservation.getQuantity());
                inventoryRepository.save(inventory);
            }
            
            reservation.setStatus(InventoryReservation.ReservationStatus.FAILED);
            reservationRepository.save(reservation);
        }
    }
    
    // ==================== Result Classes ====================
    
    @Data
    @Builder
    public static class ReservationResult {
        private boolean success;
        private String reservationId;
        private String failureReason;
        private List<InventoryReservedEvent.ReservedItem> reservedItems;
        private List<InventoryReservationFailedEvent.FailedItem> failedItems;
        
        public static ReservationResult success(String reservationId, 
                List<InventoryReservedEvent.ReservedItem> reservedItems) {
            return ReservationResult.builder()
                .success(true)
                .reservationId(reservationId)
                .reservedItems(reservedItems)
                .build();
        }
        
        public static ReservationResult failure(String reason, 
                List<InventoryReservationFailedEvent.FailedItem> failedItems) {
            return ReservationResult.builder()
                .success(false)
                .failureReason(reason)
                .failedItems(failedItems)
                .build();
        }
    }
}

package com.fiinx.inventory.application.service;

import com.fiinx.inventory.application.dto.InventoryReservationResult;
import com.fiinx.common.event.inventory.InventoryReserveCommand;
import com.fiinx.inventory.application.dto.StockCheckResponse;
import com.fiinx.inventory.domain.entity.Inventory;
import com.fiinx.inventory.domain.repository.InventoryRepository;
import com.fiinx.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryReservationResult reserveInventory(InventoryReserveCommand command) {
        log.info("Attempting to reserve inventory for order: {}", command.getOrderId());
        List<InventoryReserveCommand.ReservationItem> reservedItems = new ArrayList<>();
        
        // Senior Note: In a production system, we would store these in a 'Reservation' table.
        // For now, we apply the reservation directly to the inventory entity.
        for (InventoryReserveCommand.ReservationItem item : command.getItems()) {
            UUID productId = UUID.fromString(item.getProductId());
            Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
            
            if (inventory.reserve(item.getQuantity())) {
                inventoryRepository.save(inventory);
                reservedItems.add(item);
            } else {
                log.warn("Insufficient stock for product {}: requested {}, available {}", 
                        productId, item.getQuantity(), inventory.getAvailableQuantity());
                return InventoryReservationResult.failure("Insufficient stock for product: " + item.getProductId(), 
                        List.of(item));
            }
        }
        
        String reservationId = UUID.randomUUID().toString();
        log.info("Successfully reserved inventory for order: {}. Reservation ID: {}", 
                command.getOrderId(), reservationId);
        
        return InventoryReservationResult.success(reservationId, reservedItems);
    }

    @Transactional
    public void releaseReservation(String reservationId) {
        // Senior Note: To properly release by ID, we need the Reservation record. 
        // As a temporary fix for compile errors, we log this.
        // TODO: Implement Reservation table to track items per reservationId.
        log.info("Releasing reservation: {}. (Requires Reservation table for full implementation)", reservationId);
    }

    @Transactional(readOnly = true)
    public StockCheckResponse checkStock(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        return StockCheckResponse.builder()
                .productId(productId)
                .availableQuantity(inventory.getAvailableQuantity())
                .isAvailable(inventory.getAvailableQuantity() > 0)
                .build();
    }

    @Transactional
    public void importStock(UUID productId, Integer quantity, String note) {
        log.info("Importing {} items for product {}", quantity, productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> Inventory.builder().productId(productId).availableQuantity(0).reservedQuantity(0).build());
        
        inventory.addStock(quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void adjustStock(UUID productId, Integer quantity, String reason) {
        log.info("Adjusting stock for product {}: {} because {}", productId, quantity, reason);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
        
        inventory.setAvailableQuantity(quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getLowStockItems(Integer threshold) {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getAvailableQuantity() <= threshold)
                .collect(Collectors.toList());
    }
}

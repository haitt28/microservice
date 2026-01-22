package com.fiinx.inventory.application.service;

import com.fiinx.inventory.application.dto.StockCheckResponse;
import com.fiinx.inventory.domain.entity.Inventory;
import com.fiinx.inventory.domain.repository.InventoryRepository;
import com.fiinx.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

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

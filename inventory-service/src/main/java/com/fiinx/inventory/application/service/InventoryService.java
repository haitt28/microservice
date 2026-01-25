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

/**
 * Senior Note: Inventory Service - Quản lý kho hàng.
 * 
 * - Xử lý việc giữ hàng (Reservation) cho đơn hàng.
 * - Kiểm tra tồn kho thời gian thực.
 * - Nhập kho và điều chỉnh số lượng tồn kho.
 * - Quản lý các mặt hàng sắp hết hàng (Low stock).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryReservationResult reserveInventory(InventoryReserveCommand command) {
        log.info("Attempting to reserve inventory for order: {}", command.getOrderId());
        List<InventoryReserveCommand.ReservationItem> reservedItems = new ArrayList<>();
        
        // Senior Note: Trong một hệ thống thực tế (Production), chúng ta nên lưu các thông tin này vào bảng 'Reservation'.
        // Hiện tại, chúng ta áp dụng việc giữ hàng trực tiếp vào thực thể Inventory.
        for (InventoryReserveCommand.ReservationItem item : command.getItems()) {
            UUID productId = UUID.fromString(item.getProductId());
            Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
            
            if (inventory.reserve(item.getQuantity())) {
                inventoryRepository.save(inventory);
                reservedItems.add(item);
            } else {
                log.warn("Không đủ hàng trong kho cho sản phẩm {}: yêu cầu {}, hiện có {}", 
                        productId, item.getQuantity(), inventory.getAvailableQuantity());
                return InventoryReservationResult.failure("Không đủ hàng trong kho cho sản phẩm: " + item.getProductId(), 
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
        // Senior Note: Để giải phóng hàng theo ID một cách chính xác, chúng ta cần bản ghi Reservation.
        // Đây là phương thức tạm thời để tránh lỗi biên dịch, xử lý thông qua ghi log.
        // TODO: Triển khai bảng Reservation để theo dõi các mặt hàng cho mỗi reservationId.
        log.info("Giải phóng hàng đã giữ: {}. (Yêu cầu bảng Reservation để triển khai đầy đủ)", reservationId);
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

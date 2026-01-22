package com.fiinx.inventory.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.inventory.application.dto.StockCheckResponse;
import com.fiinx.inventory.application.service.InventoryService;
import com.fiinx.inventory.domain.entity.Inventory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock and inventory management API")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check stock", description = "Check if a product is in stock and available quantity")
    public ResponseEntity<ApiResponse<StockCheckResponse>> checkStock(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.checkStock(productId)));
    }

    @PostMapping("/admin/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Import stock", description = "Add stock to a product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> importStock(
            @RequestParam UUID productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String note
    ) {
        inventoryService.importStock(productId, quantity, note);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock imported successfully"));
    }

    @GetMapping("/admin/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get low stock items", description = "List products with stock below threshold (Admin only)")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockItems(threshold)));
    }
}

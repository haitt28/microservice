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
@Tag(name = "Kho hàng", description = "API quản lý tồn kho và kho hàng")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/check/{productId}")
    @Operation(summary = "Kiểm tra tồn kho", description = "Kiểm tra xem sản phẩm còn hàng không và số lượng còn lại")
    public ResponseEntity<ApiResponse<StockCheckResponse>> checkStock(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.checkStock(productId)));
    }

    @PostMapping("/admin/import")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Nhập kho", description = "Thêm hàng vào kho cho một sản phẩm (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<Void>> importStock(
            @RequestParam UUID productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String note
    ) {
        inventoryService.importStock(productId, quantity, note);
        return ResponseEntity.ok(ApiResponse.success(null, "Nhập kho thành công"));
    }

    @GetMapping("/admin/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách hàng sắp hết", description = "Liệt kê các sản phẩm có số lượng dưới ngưỡng quy định (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockItems(threshold)));
    }
}

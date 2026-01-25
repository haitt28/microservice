package com.fiinx.shipping.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.shipping.application.dto.*;
import com.fiinx.shipping.application.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
@Tag(name = "Giao hàng", description = "API quản lý giao hàng và vận chuyển")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/calculate-fee")
    @Operation(summary = "Tính phí giao hàng", description = "Ước tính phí giao hàng dựa trên địa chỉ và trọng lượng")
    public ResponseEntity<ApiResponse<ShippingFeeResponse>> calculateFee(
            @Valid @RequestBody ShippingFeeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.calculateFee(request)));
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo vận đơn", description = "Tạo một vận đơn mới và lấy mã theo dõi (Nội bộ/Admin)")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.createShipment(request), "Vận đơn đã được tạo thành công"));
    }

    @GetMapping("/track/{trackingCode}")
    @Operation(summary = "Theo dõi vận đơn", description = "Lấy trạng thái và chi tiết theo dõi vận đơn mới nhất")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getTrackingInfo(
            @PathVariable String trackingCode
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getTrackingInfo(trackingCode)));
    }
}

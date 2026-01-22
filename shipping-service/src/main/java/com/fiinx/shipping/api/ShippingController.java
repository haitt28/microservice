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
@Tag(name = "Shipping", description = "Shipping and delivery API")
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/calculate-fee")
    @Operation(summary = "Calculate shipping fee", description = "Estimate shipping fee given address and weight")
    public ResponseEntity<ApiResponse<ShippingFeeResponse>> calculateFee(
            @Valid @RequestBody ShippingFeeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.calculateFee(request)));
    }

    @PostMapping("/create")
    @Operation(summary = "Create shipment", description = "Create a new shipment and get tracking code (Internal/Admin)")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.createShipment(request), "Shipment created successfully"));
    }

    @GetMapping("/track/{trackingCode}")
    @Operation(summary = "Track shipment", description = "Get latest tracking status and details")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getTrackingInfo(
            @PathVariable String trackingCode
    ) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getTrackingInfo(trackingCode)));
    }
}

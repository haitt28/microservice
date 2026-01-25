package com.fiinx.identity.api.controller;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.identity.api.dto.AddressRequest;
import com.fiinx.identity.api.dto.AddressResponse;
import com.fiinx.identity.application.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/identity/addresses")
@RequiredArgsConstructor
@Tag(name = "Địa chỉ người dùng", description = "Quản lý địa chỉ giao hàng của người dùng")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Lấy danh sách địa chỉ", description = "Liệt kê tất cả các địa chỉ đã lưu của người dùng hiện tại")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getUserAddresses(principal.getName())));
    }

    @PostMapping
    @Operation(summary = "Thêm địa chỉ", description = "Lưu một địa chỉ giao hàng mới")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            Principal principal,
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(addressService.addAddress(principal.getName(), request)));
    }
}

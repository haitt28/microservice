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
@Tag(name = "User Addresses", description = "Manage user shipping addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get user addresses", description = "List all saved addresses for the current user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getUserAddresses(principal.getName())));
    }

    @PostMapping
    @Operation(summary = "Add address", description = "Save a new shipping address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            Principal principal,
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(addressService.addAddress(principal.getName(), request)));
    }
}

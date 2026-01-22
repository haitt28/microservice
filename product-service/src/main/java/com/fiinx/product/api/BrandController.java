package com.fiinx.product.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.product.application.service.BrandService;
import com.fiinx.product.domain.entity.Brand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Brand Controller - Public endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Product brand API")
public class BrandController {
    
    private final BrandService brandService;
    
    /**
     * Get all brands
     */
    @GetMapping
    @Operation(summary = "Get brands", description = "Get all active brands")
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        List<Brand> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }
    
    /**
     * Get brand by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID", description = "Get brand details")
    public ResponseEntity<ApiResponse<Brand>> getBrandById(@PathVariable UUID id) {
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }
    
    /**
     * Get brand by slug
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get brand by slug", description = "Get brand by SEO-friendly slug")
    public ResponseEntity<ApiResponse<Brand>> getBrandBySlug(@PathVariable String slug) {
        Brand brand = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }
    
    /**
     * Search brands
     */
    @GetMapping("/search")
    @Operation(summary = "Search brands", description = "Search brands by name")
    public ResponseEntity<ApiResponse<List<Brand>>> searchBrands(
            @RequestParam String name
    ) {
        List<Brand> brands = brandService.searchBrands(name);
        return ResponseEntity.ok(ApiResponse.success(brands));
    }
}

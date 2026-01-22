package com.fiinx.product.api;

import com.fiinx.common.annotation.RateLimited;
import com.fiinx.common.dto.ApiResponse;
import com.fiinx.common.dto.PageResponse;
import com.fiinx.product.application.dto.request.CreateProductRequest;
import com.fiinx.product.application.dto.response.ProductDetailResponse;
import com.fiinx.product.application.service.ProductService;
import com.fiinx.product.domain.entity.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Senior Note: Product Controller
 * 
 * - Public endpoints cho product listing (no auth)
 * - Admin endpoints cho CRUD operations (ADMIN role)
 * - Rate limiting cho security
 * - OpenAPI documentation
 * 
 * BEST PRACTICE:
 * - Separate public/admin endpoints clearly
 * - Use @PageableDefault cho pagination
 * - Rate limiting trên expensive operations
 * - Proper HTTP status codes
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management API")
public class ProductController {
    
    private final ProductService productService;
    
    // ==================== Public Endpoints ====================
    
    /**
     * Get all products với filters
     */
    @GetMapping
    @Operation(summary = "Get products", description = "Get paginated list of products with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getProducts(
            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) UUID categoryId,
            
            @Parameter(description = "Filter by brand ID")
            @RequestParam(required = false) UUID brandId,
            
            @Parameter(description = "Minimum price")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Maximum price")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Product status (default: ACTIVE)")
            @RequestParam(required = false, defaultValue = "ACTIVE") ProductStatus status,
            
            @Parameter(description = "Filter featured products only")
            @RequestParam(required = false) Boolean featured,
            
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getProducts(
            categoryId, brandId, minPrice, maxPrice, status, featured, pageable
        );
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get detailed product information")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductById(
            @PathVariable UUID id
    ) {
        ProductDetailResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    /**
     * Get product by slug
     */
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product by slug", description = "Get product by SEO-friendly slug")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductBySlug(
            @PathVariable String slug
    ) {
        ProductDetailResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    /**
     * Search products
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Full-text search for products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> searchProducts(
            @Parameter(description = "Search keyword")
            @RequestParam String q,
            
            @PageableDefault(size = 20, sort = "soldCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.searchProducts(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Get featured products
     */
    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Get list of featured products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getFeaturedProducts(
            @PageableDefault(size = 10, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Get bestsellers
     */
    @GetMapping("/bestsellers")
    @Operation(summary = "Get bestsellers", description = "Get top selling products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getBestSellers(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getBestSellers(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Get new arrivals
     */
    @GetMapping("/new-arrivals")
    @Operation(summary = "Get new arrivals", description = "Get newly added products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getNewArrivals(
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getNewArrivals(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Get related products
     */
    @GetMapping("/{id}/related")
    @Operation(summary = "Get related products", description = "Get products in same category")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getRelatedProducts(
            @PathVariable UUID id,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getRelatedProducts(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ==================== Admin Endpoints ====================
    
    /**
     * Create new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @RateLimited(key = "'admin:product:create'", limit = 100, window = 1, timeUnit = TimeUnit.HOURS)
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("Admin creating product: {}", request.getName());
        ProductDetailResponse product = productService.createProduct(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }
    
    /**
     * Update product (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update product", description = "Update existing product (Admin only)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("Admin updating product: id={}", id);
        ProductDetailResponse product = productService.updateProduct(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    /**
     * Publish product (Admin only)
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Publish product", description = "Make product active and visible (Admin only)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> publishProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin publishing product: id={}", id);
        ProductDetailResponse product = productService.publishProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Product published successfully"));
    }
    
    /**
     * Deactivate product (Admin only)
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Deactivate product", description = "Make product inactive (Admin only)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> deactivateProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin deactivating product: id={}", id);
        ProductDetailResponse product = productService.deactivateProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Product deactivated successfully"));
    }
    
    /**
     * Delete product (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete product", description = "Soft delete product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin deleting product: id={}", id);
        productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
}

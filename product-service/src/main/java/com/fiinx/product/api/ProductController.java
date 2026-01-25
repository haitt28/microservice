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
 * Senior Note: Product Controller (Bộ điều khiển Sản phẩm)
 * 
 * - Các endpoint công khai (Public) phục vụ liệt kê sản phẩm (không yêu cầu xác thực).
 * - Các endpoint quản trị (Admin) phục vụ thao tác CRUD (yêu cầu role ADMIN).
 * - Cơ chế Rate limiting được áp dụng để đảm bảo bảo mật và hiệu năng.
 * - Tài liệu API được cấu hình qua OpenAPI (Swagger).
 * 
 * BEST PRACTICE:
 * - Phân tách rõ ràng giữa các endpoint công khai và quản trị.
 * - Sử dụng @PageableDefault để cấu hình mặc định cho việc phân trang.
 * - Áp dụng Rate limiting cho các thao tác tiêu tốn nhiều tài nguyên (expensive operations).
 * - Trả về mã lỗi HTTP (HTTP status codes) phù hợp cho từng trường hợp.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Sản phẩm", description = "API quản lý sản phẩm")
public class ProductController {
    
    private final ProductService productService;
    
    // ==================== Các Endpoint Công Khai (Public Endpoints) ====================
    
    /**
     * Get all products với filters
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Lấy danh sách sản phẩm phân trang với các bộ lọc tùy chọn")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getProducts(
            @Parameter(description = "Lọc theo ID danh mục")
            @RequestParam(required = false) UUID categoryId,
            
            @Parameter(description = "Lọc theo ID thương hiệu")
            @RequestParam(required = false) UUID brandId,
            
            @Parameter(description = "Giá tối thiểu")
            @RequestParam(required = false) BigDecimal minPrice,
            
            @Parameter(description = "Giá tối đa")
            @RequestParam(required = false) BigDecimal maxPrice,
            
            @Parameter(description = "Trạng thái sản phẩm (mặc định: ACTIVE)")
            @RequestParam(required = false, defaultValue = "ACTIVE") ProductStatus status,
            
            @Parameter(description = "Chỉ lấy các sản phẩm nổi bật (featured)")
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
    @Operation(summary = "Lấy sản phẩm theo ID", description = "Lấy thông tin chi tiết của một sản phẩm")
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
    @Operation(summary = "Lấy sản phẩm theo slug", description = "Lấy thông tin sản phẩm thông qua slug thân thiện với SEO")
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
    @Operation(summary = "Tìm kiếm sản phẩm", description = "Tìm kiếm toàn văn (Full-text search) cho các sản phẩm")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> searchProducts(
            @Parameter(description = "Từ khóa tìm kiếm")
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
    @Operation(summary = "Lấy các sản phẩm nổi bật", description = "Lấy danh sách các sản phẩm đang được làm nổi bật (featured)")
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
    @Operation(summary = "Lấy sản phẩm bán chạy", description = "Lấy danh sách các sản phẩm bán chạy nhất")
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
    @Operation(summary = "Lấy sản phẩm mới", description = "Lấy danh sách các sản phẩm mới được thêm vào")
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
    @Operation(summary = "Lấy các sản phẩm liên quan", description = "Lấy danh sách các sản phẩm cùng danh mục")
    public ResponseEntity<ApiResponse<PageResponse<ProductDetailResponse>>> getRelatedProducts(
            @PathVariable UUID id,
            @PageableDefault(size = 6) Pageable pageable
    ) {
        PageResponse<ProductDetailResponse> products = productService.getRelatedProducts(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ==================== Các Endpoint Quản Trị (Admin Endpoints) ====================
    
    /**
     * Create new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @RateLimited(key = "'admin:product:create'", limit = 100, window = 1, timeUnit = TimeUnit.HOURS)
    @Operation(summary = "Tạo sản phẩm mới", description = "Tạo một sản phẩm mới (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("Admin creating product: {}", request.getName());
        ProductDetailResponse product = productService.createProduct(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Sản phẩm đã được tạo thành công"));
    }
    
    /**
     * Update product (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin sản phẩm hiện có (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("Admin updating product: id={}", id);
        ProductDetailResponse product = productService.updateProduct(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Sản phẩm đã được cập nhật thành công"));
    }
    
    /**
     * Publish product (Admin only)
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Xuất bản sản phẩm", description = "Kích hoạt sản phẩm để hiển thị trên website (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> publishProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin publishing product: id={}", id);
        ProductDetailResponse product = productService.publishProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Sản phẩm đã được xuất bản thành công"));
    }
    
    /**
     * Deactivate product (Admin only)
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Ngừng kích hoạt sản phẩm", description = "Ẩn sản phẩm khỏi website (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> deactivateProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin deactivating product: id={}", id);
        ProductDetailResponse product = productService.deactivateProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(product, "Sản phẩm đã được ngừng kích hoạt thành công"));
    }
    
    /**
     * Delete product (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Xóa sản phẩm", description = "Xóa mềm sản phẩm (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID id
    ) {
        log.info("Admin deleting product: id={}", id);
        productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Sản phẩm đã được xóa thành công"));
    }
}

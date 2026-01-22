package com.fiinx.product.application.dto.response;

import com.fiinx.product.domain.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Senior Note: DTO cho response sản phẩm chi tiết
 * 
 * - Bao gồm tất cả thông tin cần thiết cho frontend
 * - Nested DTOs cho variants, images, brand, category
 * - Denormalized data để giảm số lượng API calls
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal currentPrice;
    private BigDecimal discountPercentage;
    private String sku;
    private ProductStatus status;
    
    // Metrics
    private Integer viewCount;
    private Integer soldCount;
    private BigDecimal averageRating;
    private Integer reviewCount;
    
    // Flags
    private Boolean featured;
    private Boolean newArrival;
    private Boolean isOnSale;
    
    // Relationships
    private BrandInfo brand;
    private CategoryInfo category;
    private List<VariantInfo> variants;
    private List<ImageInfo> images;
    private ImageInfo primaryImage;
    
    // Timestamps
    private LocalDateTime publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Brand information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {
        private UUID id;
        private String name;
        private String slug;
        private String logoUrl;
    }
    
    /**
     * Category information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private UUID id;
        private String name;
        private String slug;
        private String fullPath;
    }
    
    /**
     * Variant information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantInfo {
        private UUID id;
        private String name;
        private String sku;
        private BigDecimal price;
        private BigDecimal salePrice;
        private BigDecimal currentPrice;
        private Map<String, String> attributes;
        private String imageUrl;
        private Integer stockQuantity;
        private Boolean available;
        private Boolean isInStock;
    }
    
    /**
     * Image information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfo {
        private UUID id;
        private String url;
        private String altText;
        private Integer displayOrder;
        private Boolean isPrimary;
    }
}

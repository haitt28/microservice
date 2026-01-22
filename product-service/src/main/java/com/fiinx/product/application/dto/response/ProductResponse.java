package com.fiinx.product.application.dto.response;

import com.fiinx.product.domain.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO cho product listing (lighter version)
 * Không bao gồm description, variants để giảm payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private UUID id;
    private String name;
    private String slug;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal salePrice;
    private BigDecimal currentPrice;
    private BigDecimal discountPercentage;
    private String sku;
    private ProductStatus status;
    
    // Brand & Category info (minimal)
    private UUID brandId;
    private String brandName;
    private UUID categoryId;
    private String categoryName;
    
    // Primary image only
    private String primaryImageUrl;
    
    // Metrics
    private Integer soldCount;
    private BigDecimal averageRating;
    private Integer reviewCount;
    
    // Flags
    private Boolean featured;
    private Boolean newArrival;
    private Boolean isOnSale;
    
    private Instant createdAt;
}

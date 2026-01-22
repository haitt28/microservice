package com.fiinx.product.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Senior Note: DTO cho việc tạo sản phẩm mới
 * 
 * - Validation annotations để đảm bảo data integrity
 * - Nested DTOs cho variants và images
 * - Builder pattern cho flexibility
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 500, message = "Product name must not exceed 500 characters")
    private String name;
    
    @Size(max = 1000, message = "Short description must not exceed 1000 characters")
    private String shortDescription;
    
    private String description;
    
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
    private BigDecimal salePrice;
    
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;
    
    private UUID brandId;
    
    private UUID categoryId;
    
    @Builder.Default
    private Boolean featured = false;
    
    @Builder.Default
    private Boolean newArrival = false;
    
    private List<CreateVariantRequest> variants;
    
    private List<CreateImageRequest> images;
    
    /**
     * Nested DTO cho variant
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateVariantRequest {
        
        @NotBlank(message = "Variant name is required")
        private String name;
        
        @NotBlank(message = "Variant SKU is required")
        private String sku;
        
        @NotNull(message = "Variant price is required")
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal price;
        
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal salePrice;
        
        private java.util.Map<String, String> attributes;
        
        private String imageUrl;
    }
    
    /**
     * Nested DTO cho image
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateImageRequest {
        
        @NotBlank(message = "Image URL is required")
        private String url;
        
        private String altText;
        
        @Builder.Default
        private Integer displayOrder = 0;
        
        @Builder.Default
        private Boolean isPrimary = false;
    }
}

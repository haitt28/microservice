package com.fiinx.product.application.mapper;

import com.fiinx.product.application.dto.request.CreateProductRequest;
import com.fiinx.product.application.dto.response.ProductDetailResponse;
import com.fiinx.product.domain.entity.*;
import org.mapstruct.*;

import java.util.List;

/**
 * Senior Note: MapStruct Mapper cho Product
 * 
 * - Tự động generate implementation code
 * - Xử lý nested mappings (variants, images)
 * - Custom mappings cho calculated fields
 * - Update existing entity từ DTO
 * 
 * BEST PRACTICE:
 * - componentModel = "spring" để inject vào Spring container
 * - unmappedTargetPolicy = IGNORE để skip các field không map
 * - MappingTarget để update existing entity
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {CategoryMapper.class, BrandMapper.class}
)
public interface ProductMapper {
    
    /**
     * Convert CreateProductRequest to Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true) // Generate trong service
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "soldCount", constant = "0")
    @Mapping(target = "averageRating", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "reviewCount", constant = "0")
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(CreateProductRequest request);
    
    /**
     * Convert Product to ProductDetailResponse
     */
    @Mapping(target = "currentPrice", expression = "java(product.getCurrentPrice())")
    @Mapping(target = "discountPercentage", expression = "java(product.getDiscountPercentage())")
    @Mapping(target = "isOnSale", expression = "java(product.isOnSale())")
    @Mapping(target = "brand", source = "brand")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "primaryImage", expression = "java(toPrimaryImageInfo(product.getPrimaryImage()))")
    ProductDetailResponse toDetailResponse(Product product);
    
    /**
     * Convert list of Products to list of ProductDetailResponse
     */
    List<ProductDetailResponse> toDetailResponseList(List<Product> products);
    
    /**
     * Update existing Product from request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(CreateProductRequest request, @MappingTarget Product product);
    
    /**
     * Map Brand to BrandInfo
     */
    @Mapping(target = "fullPath", expression = "java(category != null ? category.getFullPath() : null)")
    ProductDetailResponse.CategoryInfo toCategoryInfo(Category category);
    
    /**
     * Map Brand to BrandInfo
     */
    ProductDetailResponse.BrandInfo toBrandInfo(Brand brand);
    
    /**
     * Map ProductVariant to VariantInfo
     */
    @Mapping(target = "currentPrice", expression = "java(variant.getCurrentPrice())")
    @Mapping(target = "isInStock", expression = "java(variant.isInStock())")
    ProductDetailResponse.VariantInfo toVariantInfo(ProductVariant variant);
    
    /**
     * Map list of variants
     */
    List<ProductDetailResponse.VariantInfo> toVariantInfoList(List<ProductVariant> variants);
    
    /**
     * Map ProductImage to ImageInfo
     */
    ProductDetailResponse.ImageInfo toImageInfo(ProductImage image);
    
    /**
     * Map list of images
     */
    List<ProductDetailResponse.ImageInfo> toImageInfoList(List<ProductImage> images);
    
    /**
     * Map primary image (có thể null)
     */
    default ProductDetailResponse.ImageInfo toPrimaryImageInfo(ProductImage image) {
        return image != null ? toImageInfo(image) : null;
    }
    
    /**
     * Map CreateVariantRequest to ProductVariant
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stockQuantity", constant = "0")
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "version", ignore = true)
    ProductVariant variantRequestToEntity(CreateProductRequest.CreateVariantRequest request);
    
    /**
     * Map CreateImageRequest to ProductImage
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage imageRequestToEntity(CreateProductRequest.CreateImageRequest request);
}

package com.fiinx.product.application.service;

import com.fiinx.common.dto.PageResponse;
import com.fiinx.common.exception.BusinessException;
import com.fiinx.common.exception.ResourceNotFoundException;
import com.fiinx.product.infrastructure.kafka.ProductEventPublisher;
import com.fiinx.common.event.product.ProductCreatedEvent;
import com.fiinx.common.event.product.ProductUpdatedEvent;
import com.fiinx.common.event.product.ProductDeletedEvent;
import com.fiinx.product.application.dto.request.CreateProductRequest;
import com.fiinx.product.application.dto.response.ProductDetailResponse;
import com.fiinx.product.application.mapper.ProductMapper;
import com.fiinx.product.domain.entity.*;
import com.fiinx.product.domain.repository.BrandRepository;
import com.fiinx.product.domain.repository.CategoryRepository;
import com.fiinx.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Senior Note: Product Service với comprehensive business logic
 * 
 * - Slug generation tự động từ name
 * - Redis caching cho performance
 * - Event publishing qua Kafka (sẽ implement)
 * - Transactional boundaries rõ ràng
 * - Error handling với custom exceptions
 * 
 * BEST PRACTICE:
 * - @Transactional cho write operations
 * - @Cacheable/@CacheEvict cho Redis
 * - Validate business rules trước khi persist
 * - Rich domain model - để logic trong entities
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final RedissonClient redissonClient;
    private final ProductEventPublisher eventPublisher;
    
    private static final String CACHE_PREFIX_DETAIL = "product:detail:";
    private static final Duration CACHE_TTL_DETAIL = Duration.ofHours(1);
    
    // ==================== Create Operations ====================
    
    /**
     * Tạo sản phẩm mới
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        // Validate unique constraints
        validateUniqueConstraints(request.getSku(), null);
        
        // Map DTO to entity
        Product product = productMapper.toEntity(request);
        
        // Generate slug
        product.setSlug(generateSlug(request.getName()));
        
        // Set relationships
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
            product.setBrand(brand);
        }
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        
        // Handle variants
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            request.getVariants().forEach(variantReq -> {
                ProductVariant variant = productMapper.variantRequestToEntity(variantReq);
                product.addVariant(variant);
            });
        }
        
        // Handle images
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            request.getImages().forEach(imageReq -> {
                ProductImage image = productMapper.imageRequestToEntity(imageReq);
                product.addImage(image);
            });
            
            // Set primary image
            ProductImage primaryImage = product.getImages().stream()
                .filter(ProductImage::getIsPrimary)
                .findFirst()
                .orElse(product.getImages().get(0));
            product.setPrimaryImage(primaryImage);
        }
        
        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Update category/brand product count
        updateProductCounts(savedProduct.getCategory(), savedProduct.getBrand(), 1);
        
        // Publish event
        eventPublisher.publishProductCreated(ProductCreatedEvent.builder()
            .productId(savedProduct.getId())
            .name(savedProduct.getName())
            .slug(savedProduct.getSlug())
            .sku(savedProduct.getSku())
            .basePrice(savedProduct.getBasePrice())
            .brandId(savedProduct.getBrand() != null ? savedProduct.getBrand().getId().toString() : null)
            .categoryId(savedProduct.getCategory() != null ? savedProduct.getCategory().getId().toString() : null)
            .build());
        
        log.info("Product created successfully: id={}, sku={}", savedProduct.getId(), savedProduct.getSku());
        
        return productMapper.toDetailResponse(savedProduct);
    }
    
    // ==================== Read Operations ====================
    
    /**
     * Lấy sản phẩm theo ID (với cache)
     */
    @Cacheable(value = "products", key = "#id")
    public ProductDetailResponse getProductById(UUID id) {
        Product product = productRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Increment view count async (không block response)
        incrementViewCountAsync(id);
        
        return productMapper.toDetailResponse(product);
    }
    
    /**
     * Lấy sản phẩm theo slug
     */
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        
        incrementViewCountAsync(product.getId());
        
        return productMapper.toDetailResponse(product);
    }
    
    /**
     * Lấy danh sách sản phẩm với filter
     */
    public PageResponse<ProductDetailResponse> getProducts(
            UUID categoryId,
            UUID brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status,
            Boolean featured,
            Pageable pageable) {
        
        Page<Product> page;
        
        if (categoryId != null) {
            page = productRepository.findByCategoryIdAndStatus(categoryId, 
                status != null ? status : ProductStatus.ACTIVE, pageable);
        } else if (brandId != null) {
            page = productRepository.findByBrandIdAndStatus(brandId, 
                status != null ? status : ProductStatus.ACTIVE, pageable);
        } else if (featured != null && featured) {
            page = productRepository.findByFeaturedTrueAndStatus(
                status != null ? status : ProductStatus.ACTIVE, pageable);
        } else if (minPrice != null && maxPrice != null) {
            page = productRepository.findByPriceRange(
                status != null ? status : ProductStatus.ACTIVE, 
                minPrice, maxPrice, pageable);
        } else {
            page = productRepository.findByStatus(
                status != null ? status : ProductStatus.ACTIVE, pageable);
        }
        
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    /**
     * Search sản phẩm theo keyword
     */
    public PageResponse<ProductDetailResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchByKeyword(ProductStatus.ACTIVE, keyword, pageable);
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    /**
     * Lấy sản phẩm featured
     */
    public PageResponse<ProductDetailResponse> getFeaturedProducts(Pageable pageable) {
        Page<Product> page = productRepository.findByFeaturedTrueAndStatus(ProductStatus.ACTIVE, pageable);
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    /**
     * Lấy sản phẩm bán chạy
     */
    public PageResponse<ProductDetailResponse> getBestSellers(Pageable pageable) {
        Page<Product> page = productRepository.findBestSellers(ProductStatus.ACTIVE, pageable);
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    /**
     * Lấy sản phẩm mới
     */
    public PageResponse<ProductDetailResponse> getNewArrivals(Pageable pageable) {
        Page<Product> page = productRepository.findByNewArrivalTrueAndStatus(ProductStatus.ACTIVE, pageable);
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    /**
     * Lấy sản phẩm liên quan
     */
    public PageResponse<ProductDetailResponse> getRelatedProducts(UUID productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (product.getCategory() == null) {
            return PageResponse.of(List.of(), 0, 0, 0, 0);
        }
        
        Page<Product> page = productRepository.findRelatedProducts(
            product.getCategory().getId(),
            productId,
            ProductStatus.ACTIVE,
            pageable
        );
        
        return PageResponse.from(page.map(productMapper::toDetailResponse));
    }
    
    // ==================== Update Operations ====================
    
    /**
     * Cập nhật sản phẩm
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDetailResponse updateProduct(UUID id, CreateProductRequest request) {
        log.info("Updating product: id={}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Validate SKU uniqueness (if changed)
        if (!product.getSku().equals(request.getSku())) {
            validateUniqueConstraints(request.getSku(), id);
        }
        
        // Update basic fields
        productMapper.updateEntityFromRequest(request, product);
        
        // Update relationships if changed
        if (request.getBrandId() != null && 
            (product.getBrand() == null || !product.getBrand().getId().equals(request.getBrandId()))) {
            Brand newBrand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
            product.setBrand(newBrand);
        }
        
        if (request.getCategoryId() != null && 
            (product.getCategory() == null || !product.getCategory().getId().equals(request.getCategoryId()))) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(newCategory);
        }
        
        product = productRepository.save(product);
        
        // Publish event
        eventPublisher.publishProductUpdated(ProductUpdatedEvent.builder()
            .productId(product.getId())
            .name(product.getName())
            .slug(product.getSlug())
            .sku(product.getSku())
            .basePrice(product.getBasePrice())
            .salePrice(product.getSalePrice())
            .status(product.getStatus().name())
            .build());
        
        log.info("Product updated successfully: id={}", id);
        
        return productMapper.toDetailResponse(product);
    }
    
    /**
     * Publish sản phẩm
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDetailResponse publishProduct(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.publish();
        product = productRepository.save(product);
        
        log.info("Product published: id={}", id);
        
        return productMapper.toDetailResponse(product);
    }
    
    /**
     * Deactivate sản phẩm
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDetailResponse deactivateProduct(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.deactivate();
        product = productRepository.save(product);
        
        log.info("Product deactivated: id={}", id);
        
        return productMapper.toDetailResponse(product);
    }
    
    // ==================== Delete Operations ====================
    
    /**
     * Soft delete sản phẩm
     */
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.softDelete();
        productRepository.save(product);
        
        // Update counts
        updateProductCounts(product.getCategory(), product.getBrand(), -1);
        
        // Publish event
        eventPublisher.publishProductDeleted(ProductDeletedEvent.builder()
            .productId(product.getId())
            .build());
        
        log.info("Product soft deleted: id={}", id);
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Generate SEO-friendly slug từ name
     */
    private String generateSlug(String name) {
        String slug = name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .trim();
        
        // Ensure uniqueness
        String finalSlug = slug;
        int counter = 1;
        while (productRepository.existsBySlug(finalSlug)) {
            finalSlug = slug + "-" + counter++;
        }
        
        return finalSlug;
    }
    
    /**
     * Validate unique constraints
     */
    private void validateUniqueConstraints(String sku, UUID excludeId) {
        if (productRepository.existsBySku(sku)) {
            Product existing = productRepository.findBySku(sku).orElse(null);
            if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
                throw new BusinessException("DUPLICATE_SKU", "SKU already exists: " + sku, HttpStatus.BAD_REQUEST);
            }
        }
    }
    
    /**
     * Increment view count async
     */
    private void incrementViewCountAsync(UUID productId) {
        // Run in separate thread to not block response
        new Thread(() -> {
            try {
                productRepository.incrementViewCount(productId);
            } catch (Exception e) {
                log.error("Failed to increment view count for product: {}", productId, e);
            }
        }).start();
    }
    
    /**
     * Update product counts cho category và brand
     */
    private void updateProductCounts(Category category, Brand brand, int delta) {
        if (category != null) {
            categoryRepository.incrementProductCount(category.getId(), delta);
        }
        if (brand != null) {
            brandRepository.incrementProductCount(brand.getId(), delta);
        }
    }
}

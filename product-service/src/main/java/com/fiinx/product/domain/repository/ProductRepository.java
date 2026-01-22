package com.fiinx.product.domain.repository;

import com.fiinx.product.domain.entity.Product;
import com.fiinx.product.domain.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Senior Note: Product Repository
 * 
 * - Extends JpaSpecificationExecutor cho dynamic queries (filtering)
 * - Custom queries với @Query cho performance
 * - Method naming convention cho Spring Data JPA
 * 
 * BEST PRACTICE:
 * - Sử dụng Optional<> cho single result queries
 * - JOIN FETCH để tránh N+1 problem
 * - @EntityGraph cho lazy loading optimization
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, 
                                          JpaSpecificationExecutor<Product> {
    
    /**
     * Tìm sản phẩm theo slug (SEO-friendly URL)
     */
    Optional<Product> findBySlug(String slug);
    
    /**
     * Tìm sản phẩm theo SKU
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Kiểm tra slug đã tồn tại chưa
     */
    boolean existsBySlug(String slug);
    
    /**
     * Kiểm tra SKU đã tồn tại chưa
     */
    boolean existsBySku(String sku);
    
    /**
     * Lấy sản phẩm theo category với pagination
     */
    Page<Product> findByCategoryIdAndStatus(UUID categoryId, ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm theo brand với pagination
     */
    Page<Product> findByBrandIdAndStatus(UUID brandId, ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm theo status
     */
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm featured
     */
    Page<Product> findByFeaturedTrueAndStatus(ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm mới
     */
    Page<Product> findByNewArrivalTrueAndStatus(ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm bán chạy (top sellers)
     * Sắp xếp theo soldCount giảm dần
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.soldCount DESC")
    Page<Product> findBestSellers(@Param("status") ProductStatus status, Pageable pageable);
    
    /**
     * Lấy sản phẩm theo khoảng giá
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status " +
           "AND p.basePrice BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(
        @Param("status") ProductStatus status,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    /**
     * Tìm kiếm sản phẩm theo tên (simple search)
     * Sử dụng ILIKE cho case-insensitive search
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(
        @Param("status") ProductStatus status,
        @Param("keyword") String keyword,
        Pageable pageable
    );
    
    /**
     * Lấy sản phẩm với variants và images (eager loading)
     * Sử dụng JOIN FETCH để tránh N+1 problem
     */
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.variants " +
           "LEFT JOIN FETCH p.images " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") UUID id);
    
    /**
     * Lấy sản phẩm với brand và category
     */
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.category " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithBrandAndCategory(@Param("id") UUID id);
    
    /**
     * Đếm số sản phẩm theo category
     */
    long countByCategoryId(UUID categoryId);
    
    /**
     * Đếm số sản phẩm theo brand
     */
    long countByBrandId(UUID brandId);
    
    /**
     * Tăng view count
     * Sử dụng @Modifying cho UPDATE query
     */
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id);
    
    /**
     * Tăng sold count
     */
    @Modifying
    @Query("UPDATE Product p SET p.soldCount = p.soldCount + :quantity WHERE p.id = :id")
    void incrementSoldCount(@Param("id") UUID id, @Param("quantity") int quantity);
    
    /**
     * Cập nhật rating
     */
    @Modifying
    @Query("UPDATE Product p SET p.averageRating = :rating, p.reviewCount = :count WHERE p.id = :id")
    void updateRating(@Param("id") UUID id, @Param("rating") BigDecimal rating, @Param("count") int count);
    
    /**
     * Lấy sản phẩm liên quan (cùng category, khác id)
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND p.id <> :excludeId AND p.status = :status")
    Page<Product> findRelatedProducts(
        @Param("categoryId") UUID categoryId,
        @Param("excludeId") UUID excludeId,
        @Param("status") ProductStatus status,
        Pageable pageable
    );
    
    /**
     * Lấy sản phẩm có rating cao
     */
    @Query("SELECT p FROM Product p WHERE p.status = :status " +
           "AND p.reviewCount > 0 ORDER BY p.averageRating DESC, p.reviewCount DESC")
    Page<Product> findTopRatedProducts(@Param("status") ProductStatus status, Pageable pageable);
    
    /**
     * Lấy danh sách sản phẩm theo IDs
     */
    List<Product> findByIdIn(List<UUID> ids);
}

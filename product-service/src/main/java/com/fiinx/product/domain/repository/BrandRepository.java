package com.fiinx.product.domain.repository;

import com.fiinx.product.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Senior Note: Brand Repository
 * 
 * - Simple CRUD operations cho Brand entity
 * - Methods để lấy active brands
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    
    /**
     * Tìm brand theo slug
     */
    Optional<Brand> findBySlug(String slug);
    
    /**
     * Kiểm tra slug đã tồn tại chưa
     */
    boolean existsBySlug(String slug);
    
    /**
     * Lấy tất cả active brands
     */
    List<Brand> findByActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * Lấy brands theo tên (search)
     */
    List<Brand> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    
    /**
     * Cập nhật product count
     */
    @Modifying
    @Query("UPDATE Brand b SET b.productCount = :count WHERE b.id = :id")
    void updateProductCount(@Param("id") UUID id, @Param("count") int count);
    
    /**
     * Tăng product count
     */
    @Modifying
    @Query("UPDATE Brand b SET b.productCount = b.productCount + :delta WHERE b.id = :id")
    void incrementProductCount(@Param("id") UUID id, @Param("delta") int delta);
}

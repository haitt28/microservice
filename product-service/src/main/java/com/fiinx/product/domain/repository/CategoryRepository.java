package com.fiinx.product.domain.repository;

import com.fiinx.product.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Senior Note: Category Repository
 * 
 * - Hỗ trợ hierarchical queries cho cây danh mục
 * - Methods để lấy root categories và children
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    /**
     * Tìm category theo slug
     */
    Optional<Category> findBySlug(String slug);
    
    /**
     * Kiểm tra slug đã tồn tại chưa
     */
    boolean existsBySlug(String slug);
    
    /**
     * Lấy tất cả root categories (không có parent)
     */
    List<Category> findByParentIsNullAndActiveTrue();
    
    /**
     * Lấy children của một category
     */
    List<Category> findByParentIdAndActiveTrue(UUID parentId);
    
    /**
     * Lấy tất cả active categories
     */
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * Lấy category với children (eager loading)
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Category> findByIdWithChildren(@Param("id") UUID id);
    
    /**
     * Cập nhật product count
     */
    @Modifying
    @Query("UPDATE Category c SET c.productCount = :count WHERE c.id = :id")
    void updateProductCount(@Param("id") UUID id, @Param("count") int count);
    
    /**
     * Tăng product count
     */
    @Modifying
    @Query("UPDATE Category c SET c.productCount = c.productCount + :delta WHERE c.id = :id")
    void incrementProductCount(@Param("id") UUID id, @Param("delta") int delta);
}

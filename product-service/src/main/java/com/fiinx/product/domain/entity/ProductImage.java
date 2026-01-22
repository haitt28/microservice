package com.fiinx.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Senior Note: ProductImage Entity
 * 
 * - Quản lý hình ảnh của sản phẩm
 * - Hỗ trợ nhiều hình ảnh cho một sản phẩm
 * - Display order để sắp xếp thứ tự hiển thị
 * - Primary flag để đánh dấu hình ảnh chính
 * 
 * BEST PRACTICE:
 * - Không extend BaseEntity vì không cần audit fields
 * - Sử dụng @ManyToOne với FetchType.LAZY để tránh N+1 problem
 */
@Entity
@Table(name = "product_images", indexes = {
    @Index(name = "idx_product_image_product", columnList = "product_id"),
    @Index(name = "idx_product_image_primary", columnList = "is_primary")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Sản phẩm mà hình ảnh này thuộc về
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    /**
     * URL của hình ảnh (từ Media Service hoặc CDN)
     */
    @Column(nullable = false, length = 1000)
    private String url;
    
    /**
     * Alt text cho SEO và accessibility
     */
    @Column(length = 255)
    private String altText;
    
    /**
     * Thứ tự hiển thị (số nhỏ hơn hiển thị trước)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
    
    /**
     * Đánh dấu hình ảnh chính (thumbnail)
     * Chỉ có 1 hình ảnh primary cho mỗi sản phẩm
     */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
}

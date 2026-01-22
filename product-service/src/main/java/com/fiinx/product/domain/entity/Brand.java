package com.fiinx.product.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Senior Note: Brand Entity
 * 
 * - Quản lý thông tin thương hiệu sản phẩm
 * - Slug cho SEO-friendly URLs
 * - Logo và website URL cho branding
 * 
 * BEST PRACTICE:
 * - Index trên slug cho query performance
 * - Soft delete thông qua active flag
 */
@Entity
@Table(name = "brands", indexes = {
    @Index(name = "idx_brand_slug", columnList = "slug", unique = true),
    @Index(name = "idx_brand_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 500)
    private String logoUrl;
    
    @Column(length = 500)
    private String websiteUrl;
    
    /**
     * Quốc gia xuất xứ
     */
    @Column(length = 100)
    private String country;
    
    /**
     * Trạng thái hoạt động
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    /**
     * Số lượng sản phẩm của thương hiệu (denormalized)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer productCount = 0;
    
    /**
     * Thứ tự hiển thị (cho featured brands)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}

package com.fiinx.product.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Senior Note: Category Entity với Hierarchical Structure
 * 
 * - Sử dụng Self-referencing relationship để tạo cây danh mục đa cấp
 * - Lazy loading cho children để tránh N+1 query problem
 * - Slug field cho SEO-friendly URLs
 * - Display order cho việc sắp xếp hiển thị
 * 
 * BEST PRACTICE:
 * - Sử dụng @Builder.Default cho collections để tránh NullPointerException
 * - Orphan removal = true để tự động xóa children khi xóa parent
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_slug", columnList = "slug", unique = true),
    @Index(name = "idx_category_parent", columnList = "parent_id"),
    @Index(name = "idx_category_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Self-referencing relationship cho cây danh mục
     * Ví dụ: Electronics > Smartphones > iPhone
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    /**
     * Danh sách danh mục con
     * CascadeType.ALL: Khi xóa parent, tự động xóa children
     * orphanRemoval = true: Xóa children khi remove khỏi collection
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> children = new ArrayList<>();
    
    @Column(length = 500)
    private String imageUrl;
    
    /**
     * Thứ tự hiển thị (số nhỏ hơn hiển thị trước)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
    
    /**
     * Trạng thái hoạt động
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    /**
     * Số lượng sản phẩm trong danh mục (denormalized)
     * Cập nhật qua event khi có sản phẩm mới/xóa
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer productCount = 0;
    
    // ==================== Helper Methods ====================
    
    /**
     * Thêm danh mục con
     */
    public void addChild(Category child) {
        children.add(child);
        child.setParent(this);
    }
    
    /**
     * Xóa danh mục con
     */
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
    
    /**
     * Kiểm tra có phải danh mục gốc (root) không
     */
    public boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Kiểm tra có danh mục con không
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    /**
     * Lấy đường dẫn đầy đủ của danh mục
     * Ví dụ: "Electronics / Smartphones / iPhone"
     */
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " / " + name;
    }
}

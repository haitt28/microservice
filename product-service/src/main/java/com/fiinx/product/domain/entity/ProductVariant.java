package com.fiinx.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Senior Note: ProductVariant Entity
 * 
 * - Quản lý các biến thể của sản phẩm (màu sắc, kích thước, v.v.)
 * - Mỗi variant có giá riêng, SKU riêng
 * - Sử dụng JSON column để lưu attributes linh hoạt
 * - Stock quantity được denormalize từ Inventory Service
 * 
 * BEST PRACTICE:
 * - @JdbcTypeCode(SqlTypes.JSON) cho PostgreSQL JSONB
 * - Lazy loading cho product để tránh N+1
 * - Optimistic locking với @Version
 */
@Entity
@Table(name = "product_variants", indexes = {
    @Index(name = "idx_variant_product", columnList = "product_id"),
    @Index(name = "idx_variant_sku", columnList = "sku", unique = true),
    @Index(name = "idx_variant_available", columnList = "available")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Sản phẩm gốc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    /**
     * Tên biến thể (ví dụ: "Đỏ - XL", "Xanh - M")
     */
    @Column(nullable = false, length = 255)
    private String name;
    
    /**
     * SKU riêng cho biến thể
     */
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    
    /**
     * Giá của biến thể (có thể khác giá base)
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    /**
     * Giá khuyến mãi
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;
    
    /**
     * Thuộc tính của biến thể dạng JSON
     * Ví dụ: {"color": "Red", "size": "XL", "material": "Cotton"}
     * 
     * BEST PRACTICE: Sử dụng JSONB trong PostgreSQL cho query performance
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();
    
    /**
     * URL hình ảnh riêng cho biến thể (nếu có)
     */
    @Column(length = 1000)
    private String imageUrl;
    
    /**
     * Số lượng tồn kho (denormalized từ Inventory Service)
     * Cập nhật qua Kafka event
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    
    /**
     * Trạng thái có sẵn để bán
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;
    
    /**
     * Optimistic locking
     */
    @Version
    private Long version;
    
    // ==================== Helper Methods ====================
    
    /**
     * Kiểm tra còn hàng không
     */
    public boolean isInStock() {
        return available && stockQuantity > 0;
    }
    
    /**
     * Lấy giá hiện tại (ưu tiên sale price)
     */
    public BigDecimal getCurrentPrice() {
        return salePrice != null ? salePrice : price;
    }
    
    /**
     * Kiểm tra có đang giảm giá không
     */
    public boolean isOnSale() {
        return salePrice != null && salePrice.compareTo(price) < 0;
    }
    
    /**
     * Tính % giảm giá
     */
    public BigDecimal getDiscountPercentage() {
        if (!isOnSale()) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = price.subtract(salePrice);
        return discount.divide(price, 2, BigDecimal.ROUND_HALF_UP)
                      .multiply(BigDecimal.valueOf(100));
    }
}

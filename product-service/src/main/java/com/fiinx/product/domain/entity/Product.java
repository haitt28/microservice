package com.fiinx.product.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Senior Note: Product Entity - Aggregate Root (Thực thể sản phẩm - Gốc kết tập)
 * 
 * - Đây là Aggregate Root trong mô hình DDD (Domain-Driven Design).
 * - Quản lý toàn bộ vòng đời (lifecycle) của Sản phẩm và các thực thể liên quan.
 * - Sử dụng CascadeType.ALL cho các biến thể (variants) và hình ảnh (images).
 * - Phi chuẩn hóa (Denormalize) một số trường (viewCount, soldCount, rating) để tối ưu hiệu năng (performance).
 * 
 * BEST PRACTICE:
 * - Sử dụng Lazy loading cho các mối quan hệ để tránh lỗi N+1 query.
 * - Orphan removal cho variants và images để tự động dọn dẹp dữ liệu rác.
 * - Helper methods để đảm bảo tính nhất quán của logic nghiệp vụ (business logic consistency).
 * - Optimistic locking thông qua trường @Version kế thừa từ BaseEntity.
 * - Cấu hình Indexes cho các trường thường xuyên được sử dụng để truy vấn.
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_slug", columnList = "slug", unique = true),
    @Index(name = "idx_product_sku", columnList = "sku", unique = true),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_brand", columnList = "brand_id"),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_featured", columnList = "featured"),
    @Index(name = "idx_product_new_arrival", columnList = "new_arrival"),
    @Index(name = "idx_product_published", columnList = "published_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {
    
    // ==================== Thông tin cơ bản (Basic Information) ====================
    
    @Column(nullable = false, length = 500)
    private String name;
    
    /**
     * SEO-friendly URL slug
     * Ví dụ: "iphone-15-pro-max-256gb"
     */
    @Column(nullable = false, unique = true, length = 500)
    private String slug;
    
    /**
     * Mô tả chi tiết (HTML supported)
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Mô tả ngắn gọn (cho listing page)
     */
    @Column(length = 1000)
    private String shortDescription;
    
    // ==================== Thông tin giá (Pricing) ====================
    
    /**
     * Giá gốc
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
    
    /**
     * Giá khuyến mãi (nếu có)
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;
    
    /**
     * SKU (Stock Keeping Unit) - mã sản phẩm
     */
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    
    // ==================== Các mối quan hệ (Relationships) ====================
    
    /**
     * Thương hiệu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    /**
     * Danh mục
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    /**
     * Trạng thái sản phẩm
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;
    
    /**
     * Danh sách biến thể (variants)
     * CascadeType.ALL: Tất cả operations cascade xuống variants
     * orphanRemoval = true: Tự động xóa variant khi remove khỏi list
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
    
    /**
     * Danh sách hình ảnh
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
    
    // ==================== Các chỉ số (Metrics - Denormalized) ====================
    
    /**
     * Số lượt xem
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;
    
    /**
     * Số lượng đã bán
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer soldCount = 0;
    
    /**
     * Điểm đánh giá trung bình (1-5)
     */
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    /**
     * Số lượng đánh giá
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;
    
    // ==================== Các cờ đánh dấu (Flags) ====================
    
    /**
     * Sản phẩm nổi bật (featured)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;
    
    /**
     * Sản phẩm mới (new arrival)
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean newArrival = false;
    
    /**
     * Thời điểm xuất bản
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    // ==================== Các phương thức Domain (Domain Methods) ====================
    
    /**
     * Thêm variant vào sản phẩm
     * BEST PRACTICE: Đảm bảo tính nhất quán của mối quan hệ hai chiều (Bidirectional relationship consistency).
     */
    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }
    
    /**
     * Xóa variant
     */
    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }
    
    /**
     * Thêm hình ảnh
     */
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }
    
    /**
     * Xóa hình ảnh
     */
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
    
    /**
     * Đặt hình ảnh chính
     * Chỉ có 1 hình ảnh primary
     */
    public void setPrimaryImage(ProductImage newPrimary) {
        // Bỏ primary của tất cả images
        images.forEach(img -> img.setIsPrimary(false));
        // Đặt primary cho image mới
        newPrimary.setIsPrimary(true);
    }
    
    /**
     * Lấy hình ảnh chính
     */
    public ProductImage getPrimaryImage() {
        return images.stream()
                .filter(ProductImage::getIsPrimary)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }
    
    /**
     * Xuất bản sản phẩm
     */
    public void publish() {
        this.status = ProductStatus.ACTIVE;
        this.publishedAt = LocalDateTime.now();
    }
    
    /**
     * Ngừng bán
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }
    
    /**
     * Đánh dấu hết hàng
     */
    public void markOutOfStock() {
        this.status = ProductStatus.OUT_OF_STOCK;
    }
    
    /**
     * Soft delete
     */
    public void softDelete() {
        this.status = ProductStatus.DELETED;
    }
    
    /**
     * Tăng view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    /**
     * Tăng sold count
     */
    public void incrementSoldCount(int quantity) {
        this.soldCount += quantity;
    }
    
    /**
     * Cập nhật rating
     * Được gọi khi có review mới từ Review Service
     */
    public void updateRating(BigDecimal newRating, int totalReviews) {
        this.averageRating = newRating;
        this.reviewCount = totalReviews;
    }
    
    /**
     * Lấy giá hiện tại (ưu tiên sale price)
     */
    public BigDecimal getCurrentPrice() {
        return salePrice != null ? salePrice : basePrice;
    }
    
    /**
     * Kiểm tra có đang giảm giá không
     */
    public boolean isOnSale() {
        return salePrice != null && salePrice.compareTo(basePrice) < 0;
    }
    
    /**
     * Tính % giảm giá
     */
    public BigDecimal getDiscountPercentage() {
        if (!isOnSale()) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = basePrice.subtract(salePrice);
        return discount.divide(basePrice, 2, BigDecimal.ROUND_HALF_UP)
                      .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Kiểm tra sản phẩm có active không
     */
    public boolean isActive() {
        return status == ProductStatus.ACTIVE;
    }
    
    /**
     * Kiểm tra có variants không
     */
    public boolean hasVariants() {
        return variants != null && !variants.isEmpty();
    }
    
    /**
     * Kiểm tra có hình ảnh không
     */
    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
}

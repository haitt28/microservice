package com.fiinx.product.domain.entity;

/**
 * Enum định nghĩa trạng thái của sản phẩm
 * 
 * BEST PRACTICE: Sử dụng enum thay vì String constants
 * để đảm bảo type safety và tránh lỗi typo
 */
public enum ProductStatus {
    /**
     * Bản nháp - chưa xuất bản
     */
    DRAFT,
    
    /**
     * Đang hoạt động - hiển thị cho khách hàng
     */
    ACTIVE,
    
    /**
     * Tạm ngưng - không hiển thị nhưng vẫn giữ dữ liệu
     */
    INACTIVE,
    
    /**
     * Hết hàng - tạm thời không bán
     */
    OUT_OF_STOCK,
    
    /**
     * Đã xóa - soft delete
     */
    DELETED
}

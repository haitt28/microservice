package com.fiinx.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Senior Note: Base Entity tích hợp sẵn các trường Auditing.
 * 
 * - Tự động quản lý các trường (createdAt, updatedAt, createdBy, updatedBy).
 * - Sử dụng Primary Key kiểu UUID (Tối ưu cho hệ thống phân tán).
 * - Triển khai cơ chế Optimistic Locking thông qua trường Version.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * Version phục vụ cơ chế Optimistic Locking.
     * Ngăn chặn việc mất dữ liệu khi có nhiều cập nhật đồng thời (Lost updates).
     */
    @Version
    @Column(name = "version")
    private Long version;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
    
    /**
     * Cờ đánh dấu xóa mềm (Soft delete).
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
    
    @Column(name = "deleted_at")
    private Instant deletedAt;
    
    // ==================== Equals & HashCode ====================
    // BEST PRACTICE: Sử dụng business key hoặc ID cho equals/hashCode.
    // Không bao giờ sử dụng các trường có thể thay đổi (mutable fields).
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        // Chỉ so sánh bằng ID nếu cả hai đối tượng đều đã được lưu vào database (persisted)
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        // Sử dụng hashCode của class để đảm bảo tính nhất quán trước khi ID được gán (assigned)
        return Objects.hash(getClass());
    }
    
    // ==================== Soft Delete ====================
    
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = Instant.now();
    }
    
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }
}

package com.fiinx.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * BEST PRACTICE #5: Base Domain Event (Sự kiện miền cơ bản)
 * 
 * - Tất cả các sự kiện (events) đều phải kế thừa từ lớp này.
 * - correlationId: Dùng để truy vết xuyên suốt vòng đời của một Saga.
 * - eventId: Mã định danh duy nhất cho mỗi sự kiện (Unique identifier).
 * - timestamp: Thời điểm xảy ra sự kiện, dùng để sắp xếp thứ tự (Event ordering).
 * - version: Phiên bản của Schema, hỗ trợ cho việc tiến hóa hệ thống (Schema evolution).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Mã định danh duy nhất của sự kiện - dùng cho cơ chế chống trùng lặp (Deduplication).
     */
    private String eventId;
    
    /**
     * Correlation ID - dùng để truy vết yêu cầu xuyên suốt Saga.
     * Được tạo tại điểm bắt đầu (Entry point) và truyền qua tất cả các Services liên quan.
     */
    private String correlationId;
    
    /**
     * Causation ID - ID của sự kiện trực tiếp gây ra sự kiện này.
     * Dùng để xây dựng chuỗi sự kiện hoặc biểu đồ sự kiện (Event chain/graph).
     */
    private String causationId;
    
    /**
     * Thời điểm xảy ra sự kiện.
     */
    private Instant timestamp;
    
    /**
     * Nguồn phát sinh sự kiện - Service nào đã phát (emit) sự kiện này.
     */
    private String source;
    
    /**
     * Loại sự kiện - Thường là tên lớp (Full qualified class name) hoặc một kiểu tùy chỉnh.
     */
    private String eventType;
    
    /**
     * Phiên bản Schema của sự kiện - hỗ trợ tính tương thích ngược (Backward compatibility).
     */
    private int version;
    
    /**
     * ID của người dùng đã kích hoạt sự kiện này (nếu có).
     */
    private String userId;
    
    /**
     * Khởi tạo các giá trị mặc định cho các trường chung.
     */
    public void initializeDefaults(String source) {
        if (this.eventId == null) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
        if (this.source == null) {
            this.source = source;
        }
        if (this.eventType == null) {
            this.eventType = this.getClass().getSimpleName();
        }
        if (this.version == 0) {
            this.version = 1;
        }
    }
    
    /**
     * Tạo một sự kiện con tham chiếu đến sự kiện này như là nguyên nhân gây ra (Cause).
     */
    public void linkAsCause(DomainEvent childEvent) {
        childEvent.setCorrelationId(this.correlationId);
        childEvent.setCausationId(this.eventId);
    }
}

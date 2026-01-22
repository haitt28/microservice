package com.fiinx.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * BEST PRACTICE #12: Outbox Pattern Entity
 * 
 * Transactional Outbox Pattern:
 * - Event được lưu cùng transaction với business data
 * - Background job poll và publish to Kafka
 * - Đảm bảo exactly-once delivery
 * - Tránh distributed transaction
 * 
 * Flow:
 * 1. Business logic + save OutboxEvent trong cùng transaction
 * 2. Scheduler poll outbox table (status = PENDING)
 * 3. Publish to Kafka
 * 4. Update status = PUBLISHED
 * 
 * Trường hợp failure:
 * - Business fail → cả business data và outbox đều rollback
 * - Kafka fail → outbox vẫn còn, retry sau
 * - App crash sau khi publish nhưng trước khi update status → message duplicate (consumer phải idempotent)
 */
@Entity
@Table(name = "outbox_events", indexes = {
    @Index(name = "idx_outbox_status", columnList = "status"),
    @Index(name = "idx_outbox_created_at", columnList = "created_at"),
    @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Aggregate type (e.g., "Order", "Payment")
     */
    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;
    
    /**
     * Aggregate ID (e.g., order ID)
     */
    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;
    
    /**
     * Event type (e.g., "OrderCreatedEvent")
     */
    @Column(name = "event_type", nullable = false, length = 200)
    private String eventType;
    
    /**
     * Event payload as JSON
     */
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;
    
    /**
     * Kafka topic to publish to
     */
    @Column(name = "topic", nullable = false, length = 200)
    private String topic;
    
    /**
     * Partition key (usually aggregate ID)
     */
    @Column(name = "partition_key", length = 100)
    private String partitionKey;
    
    /**
     * Event status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxEventStatus status = OutboxEventStatus.PENDING;
    
    /**
     * Correlation ID for distributed tracing
     */
    @Column(name = "correlation_id", length = 100)
    private String correlationId;
    
    /**
     * Number of publish attempts
     */
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;
    
    /**
     * Last error message if publish failed
     */
    @Column(name = "last_error", length = 2000)
    private String lastError;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "published_at")
    private Instant publishedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = OutboxEventStatus.PENDING;
        }
    }
    
    // ==================== Status Methods ====================
    
    public void markAsPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }
    
    public void markAsFailed(String errorMessage) {
        this.retryCount++;
        this.lastError = errorMessage;
        
        // After max retries, mark as FAILED
        if (this.retryCount >= 3) {
            this.status = OutboxEventStatus.FAILED;
        }
    }
    
    public void markAsSkipped(String reason) {
        this.status = OutboxEventStatus.SKIPPED;
        this.lastError = reason;
    }
    
    public enum OutboxEventStatus {
        PENDING,     // Waiting to be published
        PUBLISHED,   // Successfully published
        FAILED,      // Failed after max retries
        SKIPPED      // Intentionally skipped
    }
}

package com.fiinx.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * BEST PRACTICE #5: Base Domain Event
 * 
 * - Tất cả events kế thừa từ class này
 * - correlationId để track xuyên suốt saga
 * - eventId là unique identifier
 * - timestamp cho event ordering
 * - version cho schema evolution
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Unique event identifier - dùng cho deduplication
     */
    private String eventId;
    
    /**
     * Correlation ID - dùng để track request xuyên suốt saga
     * Được tạo ở entry point và pass qua tất cả services
     */
    private String correlationId;
    
    /**
     * Causation ID - ID của event gây ra event này
     * Dùng để build event chain/graph
     */
    private String causationId;
    
    /**
     * Event timestamp
     */
    private Instant timestamp;
    
    /**
     * Event source - service nào emit event
     */
    private String source;
    
    /**
     * Event type - full qualified class name hoặc custom type
     */
    private String eventType;
    
    /**
     * Schema version cho event - support backward compatibility
     */
    private int version;
    
    /**
     * User ID who triggered this event (if applicable)
     */
    private String userId;
    
    /**
     * Initialize common fields
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
     * Create a child event that references this event as cause
     */
    public void linkAsCause(DomainEvent childEvent) {
        childEvent.setCorrelationId(this.correlationId);
        childEvent.setCausationId(this.eventId);
    }
}

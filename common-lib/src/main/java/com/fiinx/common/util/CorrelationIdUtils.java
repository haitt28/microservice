package com.fiinx.common.util;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

/**
 * BEST PRACTICE #13: Correlation ID Utilities
 * 
 * Correlation ID dùng để track request xuyên suốt hệ thống:
 * - Client → API Gateway → Service A → Kafka → Service B
 * 
 * Rules:
 * 1. Generate ở entry point nếu không có
 * 2. Pass qua HTTP header "X-Correlation-ID"
 * 3. Pass qua Kafka message header
 * 4. Set vào MDC để log tự động include
 */
public final class CorrelationIdUtils {
    
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String CAUSATION_ID_HEADER = "X-Causation-ID";
    
    private CorrelationIdUtils() {
        // Utility class
    }
    
    /**
     * Generate new correlation ID
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Get correlation ID from current request context
     * Generate new one if not present
     */
    public static String getOrGenerate() {
        return getFromMDC()
                .or(CorrelationIdUtils::getFromRequest)
                .orElseGet(CorrelationIdUtils::generate);
    }
    
    /**
     * Get from MDC (SLF4J Mapped Diagnostic Context)
     */
    public static Optional<String> getFromMDC() {
        return Optional.ofNullable(MDC.get(CORRELATION_ID_MDC_KEY));
    }
    
    /**
     * Get from current HTTP request header
     */
    public static Optional<String> getFromRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String correlationId = attributes.getRequest().getHeader(CORRELATION_ID_HEADER);
                return Optional.ofNullable(correlationId);
            }
        } catch (Exception ignored) {
            // Not in HTTP request context
        }
        return Optional.empty();
    }
    
    /**
     * Set correlation ID in MDC
     */
    public static void setInMDC(String correlationId) {
        if (correlationId != null) {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        }
    }
    
    /**
     * Clear correlation ID from MDC
     */
    public static void clearMDC() {
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
    
    /**
     * Add correlation ID to HTTP headers
     */
    public static HttpHeaders addToHeaders(HttpHeaders headers, String correlationId) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        headers.set(CORRELATION_ID_HEADER, correlationId);
        return headers;
    }
    
    /**
     * Execute with correlation ID set in MDC
     */
    public static void runWithCorrelationId(String correlationId, Runnable action) {
        String previous = MDC.get(CORRELATION_ID_MDC_KEY);
        try {
            setInMDC(correlationId);
            action.run();
        } finally {
            if (previous != null) {
                setInMDC(previous);
            } else {
                clearMDC();
            }
        }
    }
}

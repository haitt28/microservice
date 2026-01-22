package com.fiinx.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Senior Note: Annotation cho cơ chế Idempotency (Tính duy nhất của hành động).
 * 
 * - Đảm bảo một Operation chỉ được thực hiện đúng một lần duy nhất dù Client gửi request nhiều lần.
 * - Key định danh thường là sự kết hợp của userId + nội dung hành động.
 * 
 * Ví dụ:
 * <pre>
 * {@code
 * // Sử dụng Idempotency key truyền lên từ Header
 * @Idempotent(keyHeader = "X-Idempotency-Key", ttl = 24, timeUnit = TimeUnit.HOURS)
 * public Payment processPayment(PaymentRequest request) { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    
    /**
     * HTTP header name containing idempotency key
     * Takes precedence over key() if present in request
     */
    String keyHeader() default "X-Idempotency-Key";
    
    /**
     * SpEL expression for idempotency key
     * Used if header is not present
     */
    String key() default "";
    
    /**
     * How long to keep idempotency record
     */
    long ttl() default 24;
    
    /**
     * Time unit for TTL
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;
    
    /**
     * If true, return cached response for duplicate requests
     * If false, just prevent re-execution
     */
    boolean cacheResponse() default true;
    
    /**
     * Key prefix for Redis storage
     */
    String prefix() default "idempotent:";
}

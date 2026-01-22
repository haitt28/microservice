package com.fiinx.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Senior Note: Annotation hỗ trợ cơ chế Rate Limiting (Giới hạn tần suất gọi API).
 * 
 * - Hỗ trợ các thuật toán như Token Bucket hoặc Sliding Window.
 * - Áp dụng cho các Method cần bảo vệ để tránh bị lạm dụng (Abuse) hoặc tấn công Brute-force.
 * 
 * Ví dụ:
 * <pre>
 * {@code
 * // Giới hạn 100 requests mỗi phút cho mỗi User
 * @RateLimited(key = "'user:' + #userId", limit = 100, window = 1, timeUnit = TimeUnit.MINUTES)
 * public Response processRequest(String userId, Request request) { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    
    /**
     * Rate limit key expression (SpEL supported)
     * Usually based on user ID, IP, or API key
     */
    String key();
    
    /**
     * Maximum number of requests allowed in the time window
     */
    int limit() default 100;
    
    /**
     * Time window duration
     */
    long window() default 1;
    
    /**
     * Time unit for the window
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    
    /**
     * Algorithm to use
     */
    Algorithm algorithm() default Algorithm.SLIDING_WINDOW;
    
    /**
     * If true, use user ID from security context as key
     */
    boolean perUser() default false;
    
    enum Algorithm {
        /**
         * Token bucket - smoother rate limiting
         */
        TOKEN_BUCKET,
        
        /**
         * Sliding window - more accurate counting
         */
        SLIDING_WINDOW,
        
        /**
         * Fixed window - simpler but can have burst at window edges
         */
        FIXED_WINDOW
    }
}

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
     * Biểu thức Rate limit key (hỗ trợ SpEL).
     * Thường dựa trên User ID, IP, hoặc API key.
     */
    String key();
    
    /**
     * Số lượng request tối đa được phép trong cửa sổ thời gian.
     */
    int limit() default 100;
    
    /**
     * Độ dài của cửa sổ thời gian (Time window).
     */
    long window() default 1;
    
    /**
     * Đơn vị thời gian cho cửa sổ (window).
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
    
    /**
     * Thuật toán sử dụng.
     */
    Algorithm algorithm() default Algorithm.SLIDING_WINDOW;
    
    /**
     * Nếu true, sẽ sử dụng User ID từ Security Context làm key.
     */
    boolean perUser() default false;
    
    enum Algorithm {
        /**
         * Token bucket - Giới hạn tần suất mượt mà hơn.
         */
        TOKEN_BUCKET,
        
        /**
         * Sliding window - Đếm chính xác hơn.
         */
        SLIDING_WINDOW,
        
        /**
         * Fixed window - Đơn giản nhưng có thể bị bùng phát (burst) ở biên cửa sổ.
         */
        FIXED_WINDOW
    }
}

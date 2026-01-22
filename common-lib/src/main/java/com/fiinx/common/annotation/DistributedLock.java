package com.fiinx.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Senior Note: Annotation cho cơ chế Distributed Lock (Khóa phân tán).
 * 
 * - Sử dụng AOP để tự động Acquire (chiếm) và Release (giải phóng) Lock.
 * - Giúp chống lại các lỗi Double-click hoặc Concurrent requests từ phía Client.
 * 
 * Ví dụ sử dụng:
 * <pre>
 * {@code
 * @DistributedLock(key = "'order:' + #orderId", waitTime = 0, leaseTime = 30)
 * public Order createOrder(String orderId, CreateOrderRequest request) {
 *     // Phương thức này chỉ được thực thi bởi một thread tại một thời điểm cho cùng một orderId
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    
    /**
     * Lock key expression (SpEL supported)
     * Can reference method parameters using # prefix
     * 
     * Examples:
     * - "'order:create:' + #orderId"
     * - "'payment:' + #request.orderId"
     * - "'user:' + #userId + ':action:' + #actionType"
     */
    String key();
    
    /**
     * Maximum time to wait for lock acquisition (in timeUnit)
     * 0 = try once (fail fast) - recommended for double-click prevention
     */
    long waitTime() default 0;
    
    /**
     * Lock lease time (auto-release after this duration)
     * Should be longer than expected method execution time
     */
    long leaseTime() default 30;
    
    /**
     * Time unit for waitTime and leaseTime
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    /**
     * Prefix for all lock keys (namespace)
     */
    String prefix() default "distributed-lock:";
}

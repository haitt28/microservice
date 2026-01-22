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
     * Biểu thức Lock key (hỗ trợ SpEL).
     * Có thể tham chiếu các tham số của phương thức bằng tiền tố #.
     */
    String key();
    
    /**
     * Thời gian tối đa để chờ lấy Lock (tính theo timeUnit).
     * 0 = chỉ thử một lần (fail fast) - khuyến nghị dùng để chống lỗi double-click.
     */
    long waitTime() default 0;
    
    /**
     * Thời gian chiếm giữ Lock (tự động giải phóng sau thời gian này).
     * Nên dài hơn thời gian thực thi dự kiến của phương thức.
     */
    long leaseTime() default 30;
    
    /**
     * Đơn vị thời gian cho waitTime và leaseTime.
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    /**
     * Tiền tố (prefix) cho tất cả các lock keys (định danh namespace).
     */
    String prefix() default "distributed-lock:";
}

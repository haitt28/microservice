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
     * Tên HTTP header chứa Idempotency key.
     * Được ưu tiên hơn key() nếu tồn tại trong request.
     */
    String keyHeader() default "X-Idempotency-Key";
    
    /**
     * Biểu thức SpEL cho Idempotency key.
     * Được sử dụng nếu header không tồn tại.
     */
    String key() default "";
    
    /**
     * Thời gian lưu trữ bản ghi Idempotency.
     */
    long ttl() default 24;
    
    /**
     * Đơn vị thời gian cho TTL.
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;
    
    /**
     * Nếu true, trả về response đã cache cho các request trùng lặp.
     * Nếu false, chỉ ngăn chặn việc thực thi lại.
     */
    boolean cacheResponse() default true;
    
    /**
     * Tiền tố (prefix) khóa cho bộ nhớ Redis.
     */
    String prefix() default "idempotent:";
}

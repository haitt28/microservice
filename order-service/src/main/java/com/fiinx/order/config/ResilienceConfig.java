package com.fiinx.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Cấu hình Resilience4j - "Tấm khiên" bảo vệ Microservices.
 * Senior Note: Trong môi trường phân tán, lỗi là điều tất yếu. 
 * Chúng ta dùng Circuit Breaker (Ngắt mạch) để ngăn chặn lỗi dây chuyền 
 * và Retry (Thử lại) để vượt qua các lỗi tạm thời (network glitch).
 */
@Configuration
public class ResilienceConfig {
    
    /**
     * Cấu hình mặc định cho Circuit Breaker (Bộ ngắt mạch).
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            // Ngưỡng tỷ lệ lỗi để kích hoạt Circuit Breaker (50%)
            .failureRateThreshold(50)
            
            // Thời gian chờ ở trạng thái OPEN trước khi chuyển sang HALF_OPEN
            .waitDurationInOpenState(Duration.ofSeconds(30))
            
            // Số lượng cuộc gọi được phép thử ở trạng thái HALF_OPEN
            .permittedNumberOfCallsInHalfOpenState(5)
            
            // Cài đặt cho cơ chế Sliding Window (Cửa sổ trượt)
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            
            // Số lượng cuộc gọi tối thiểu trước khi bắt đầu tính toán tỷ lệ lỗi
            .minimumNumberOfCalls(5)
            
            // Cài đặt cho các cuộc gọi chậm (Slow calls)
            .slowCallRateThreshold(50)
            .slowCallDurationThreshold(Duration.ofSeconds(5))
            
            .build();
    }
    
    /**
     * Cấu hình mặc định cho Retry (Cơ chế thử lại).
     */
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            
            // Chiến thuật 'Exponential Backoff': Đừng thử lại ngay lập tức.
            // Tăng dần thời gian chờ (500ms -> 1s -> 2s) để tránh làm "ngộp" service đang lỗi.
            .intervalFunction(attempt -> 
                Duration.ofMillis((long) (500 * Math.pow(2, attempt - 1))).toMillis())
            
            // Chỉ thực hiện Retry đối với một số Exception cụ thể (các lỗi tạm thời)
            .retryExceptions(
                java.net.ConnectException.class,
                java.net.SocketTimeoutException.class,
                org.springframework.web.client.ResourceAccessException.class
            )
            
            // KHÔNG thực hiện Retry đối với các Exception này (lỗi nghiệp vụ)
            .ignoreExceptions(
                com.fiinx.common.exception.BusinessException.class
            )
            
            .build();
    }
    
    /**
     * Cấu hình mặc định cho Time Limiter (Giới hạn thời gian xử lý).
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(10))
            .cancelRunningFuture(true)
            .build();
    }
}

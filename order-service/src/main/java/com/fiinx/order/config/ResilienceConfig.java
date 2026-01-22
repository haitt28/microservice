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
     * Default Circuit Breaker configuration
     */
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            // Failure rate threshold (50%)
            .failureRateThreshold(50)
            
            // Wait duration before transitioning from OPEN to HALF_OPEN
            .waitDurationInOpenState(Duration.ofSeconds(30))
            
            // Number of calls in HALF_OPEN state
            .permittedNumberOfCallsInHalfOpenState(5)
            
            // Sliding window settings
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            
            // Minimum number of calls before evaluating failure rate
            .minimumNumberOfCalls(5)
            
            // Slow call settings
            .slowCallRateThreshold(50)
            .slowCallDurationThreshold(Duration.ofSeconds(5))
            
            .build();
    }
    
    /**
     * Default Retry configuration
     */
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            
            // Chiến thuật 'Exponential Backoff': Đừng thử lại ngay lập tức.
            // Tăng dần thời gian chờ (500ms -> 1s -> 2s) để tránh làm "ngộp" service đang lỗi.
            .intervalFunction(attempt -> 
                Duration.ofMillis((long) (500 * Math.pow(2, attempt - 1))).toMillis())
            
            // Retry on specific exceptions
            .retryExceptions(
                java.net.ConnectException.class,
                java.net.SocketTimeoutException.class,
                org.springframework.web.client.ResourceAccessException.class
            )
            
            // Don't retry on these
            .ignoreExceptions(
                com.fiinx.common.exception.BusinessException.class
            )
            
            .build();
    }
    
    /**
     * Default Time Limiter configuration
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(10))
            .cancelRunningFuture(true)
            .build();
    }
}

package com.fiinx.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * BEST PRACTICE #10: Các thuộc tính cấu hình Redis.
 * 
 * Cài đặt Redis tập trung cho:
 * - Caching (Bộ nhớ đệm).
 * - Rate limiting (Giới hạn lưu lượng).
 * - Distributed locking (Khóa phân tán).
 * - Session management (Quản lý phiên làm việc).
 */
@Data
@ConfigurationProperties(prefix = "app.redis")
public class RedisProperties {
    
    /**
     * Chế độ kết nối Redis.
     */
    private Mode mode = Mode.STANDALONE;
    
    /**
     * Cấu hình chế độ Standalone.
     */
    private Standalone standalone = new Standalone();
    
    /**
     * Cấu hình chế độ Sentinel.
     */
    private Sentinel sentinel = new Sentinel();
    
    /**
     * Cấu hình chế độ Cluster.
     */
    private Cluster cluster = new Cluster();
    
    /**
     * Cấu hình cơ chế Cache.
     */
    private Cache cache = new Cache();
    
    /**
     * Cấu hình cơ chế Lock.
     */
    private Lock lock = new Lock();
    
    /**
     * Cấu hình cơ chế Rate Limiter.
     */
    private RateLimiter rateLimiter = new RateLimiter();
    
    public enum Mode {
        STANDALONE,
        SENTINEL,
        CLUSTER
    }
    
    @Data
    public static class Standalone {
        private String host = "localhost";
        private int port = 6379;
        private String password;
        private int database = 0;
    }
    
    @Data
    public static class Sentinel {
        private String master = "mymaster";
        private String nodes = "localhost:26379";
        private String password;
        private int database = 0;
    }
    
    @Data
    public static class Cluster {
        private String nodes = "localhost:7000,localhost:7001,localhost:7002";
        private String password;
        private int maxRedirects = 3;
    }
    
    @Data
    public static class Cache {
        /**
         * Thời gian sống (TTL) mặc định cho các mục trong cache.
         */
        private Duration defaultTtl = Duration.ofMinutes(30);
        
        /**
         * Số lượng entry tối đa trong cache.
         */
        private int maxEntries = 10000;
        
        /**
         * Tiền tố (prefix) cho các cache key.
         */
        private String keyPrefix = "cache:";
        
        /**
         * Bật/Tắt cơ chế caching toàn cục.
         */
        private boolean enabled = true;
    }
    
    @Data
    public static class Lock {
        /**
         * Thời gian chờ mặc định để lấy Lock.
         */
        private Duration defaultWaitTime = Duration.ofSeconds(3);
        
        /**
         * Thời gian thuê mặc định (tự động release).
         */
        private Duration defaultLeaseTime = Duration.ofSeconds(30);
        
        /**
         * Tiền tố (prefix) cho các lock key.
         */
        private String keyPrefix = "lock:";
    }
    
    @Data
    public static class RateLimiter {
        /**
         * Giới hạn mặc định (số lượng request trong 1 window).
         */
        private int defaultLimit = 100;
        
        /**
         * Thời gian window mặc định.
         */
        private Duration defaultWindow = Duration.ofMinutes(1);
        
        /**
         * Tiền tố (prefix) cho các rate limit key.
         */
        private String keyPrefix = "ratelimit:";
        
        /**
         * Bật/Tắt cơ chế rate limiting toàn cục.
         */
        private boolean enabled = true;
    }
}

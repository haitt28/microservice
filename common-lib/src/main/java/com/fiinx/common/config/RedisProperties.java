package com.fiinx.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * BEST PRACTICE #10: Redis Configuration Properties
 * 
 * Centralized Redis settings for:
 * - Caching
 * - Rate limiting
 * - Distributed locking
 * - Session management
 */
@Data
@ConfigurationProperties(prefix = "app.redis")
public class RedisProperties {
    
    /**
     * Redis connection mode
     */
    private Mode mode = Mode.STANDALONE;
    
    /**
     * Standalone configuration
     */
    private Standalone standalone = new Standalone();
    
    /**
     * Sentinel configuration
     */
    private Sentinel sentinel = new Sentinel();
    
    /**
     * Cluster configuration
     */
    private Cluster cluster = new Cluster();
    
    /**
     * Cache configuration
     */
    private Cache cache = new Cache();
    
    /**
     * Lock configuration
     */
    private Lock lock = new Lock();
    
    /**
     * Rate limiter configuration
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
         * Default TTL for cached items
         */
        private Duration defaultTtl = Duration.ofMinutes(30);
        
        /**
         * Maximum cache entries
         */
        private int maxEntries = 10000;
        
        /**
         * Cache key prefix
         */
        private String keyPrefix = "cache:";
        
        /**
         * Enable/disable caching globally
         */
        private boolean enabled = true;
    }
    
    @Data
    public static class Lock {
        /**
         * Default wait time for lock acquisition
         */
        private Duration defaultWaitTime = Duration.ofSeconds(3);
        
        /**
         * Default lease time (auto-release)
         */
        private Duration defaultLeaseTime = Duration.ofSeconds(30);
        
        /**
         * Lock key prefix
         */
        private String keyPrefix = "lock:";
    }
    
    @Data
    public static class RateLimiter {
        /**
         * Default rate limit (requests per window)
         */
        private int defaultLimit = 100;
        
        /**
         * Default window duration
         */
        private Duration defaultWindow = Duration.ofMinutes(1);
        
        /**
         * Rate limit key prefix
         */
        private String keyPrefix = "ratelimit:";
        
        /**
         * Enable rate limiting globally
         */
        private boolean enabled = true;
    }
}

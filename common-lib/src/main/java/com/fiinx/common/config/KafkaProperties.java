package com.fiinx.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * BEST PRACTICE #9: Externalized Kafka Configuration
 * 
 * Type-safe configuration binding
 * Dễ dàng override per environment
 */
@Data
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
    
    /**
     * Kafka bootstrap servers
     */
    private String bootstrapServers = "localhost:9092";
    
    /**
     * Consumer group ID prefix
     */
    private String consumerGroupPrefix = "fiinx";
    
    /**
     * Producer settings
     */
    private Producer producer = new Producer();
    
    /**
     * Consumer settings
     */
    private Consumer consumer = new Consumer();
    
    /**
     * Topic configurations
     */
    private Map<String, TopicConfig> topics = new HashMap<>();
    
    /**
     * Dead Letter Queue settings
     */
    private DeadLetterQueue dlq = new DeadLetterQueue();
    
    @Data
    public static class Producer {
        private String acks = "all";
        private int retries = 3;
        private Duration retryBackoff = Duration.ofMillis(100);
        private String compressionType = "snappy";
        private Duration lingerMs = Duration.ofMillis(5);
        private int batchSize = 16384;
        private int bufferMemory = 33554432;
        
        /**
         * Idempotence - exactly-once semantics
         */
        private boolean enableIdempotence = true;
        
        /**
         * Max in-flight requests per connection
         * Should be <=5 when idempotence is enabled
         */
        private int maxInFlightRequestsPerConnection = 5;
    }
    
    @Data
    public static class Consumer {
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = false;
        private int maxPollRecords = 100;
        private Duration maxPollInterval = Duration.ofMinutes(5);
        private Duration sessionTimeout = Duration.ofSeconds(30);
        private Duration heartbeatInterval = Duration.ofSeconds(10);
        
        /**
         * Concurrency level (number of consumer threads)
         */
        private int concurrency = 3;
        
        /**
         * Retry configuration
         */
        private int maxRetries = 3;
        private Duration retryBackoff = Duration.ofSeconds(1);
    }
    
    @Data
    public static class TopicConfig {
        private String name;
        private int partitions = 3;
        private short replicationFactor = 1;
        private Duration retentionMs = Duration.ofDays(7);
    }
    
    @Data
    public static class DeadLetterQueue {
        private boolean enabled = true;
        private String suffix = ".DLT";
        private int maxRetries = 3;
    }
    
    // ==================== Topic Names ====================
    // BEST PRACTICE: Centralized topic name constants
    
    public static final String TOPIC_ORDER_CREATED = "order.created";
    public static final String TOPIC_ORDER_COMPLETED = "order.completed";
    public static final String TOPIC_ORDER_FAILED = "order.failed";
    
    public static final String TOPIC_INVENTORY_RESERVE = "inventory.reserve.command";
    public static final String TOPIC_INVENTORY_RESERVED = "inventory.reserved";
    public static final String TOPIC_INVENTORY_FAILED = "inventory.reservation.failed";
    public static final String TOPIC_INVENTORY_RELEASE = "inventory.release.command";
    
    public static final String TOPIC_PAYMENT_PROCESS = "payment.process.command";
    public static final String TOPIC_PAYMENT_PROCESSED = "payment.processed";
    public static final String TOPIC_PAYMENT_FAILED = "payment.failed";
    public static final String TOPIC_PAYMENT_REFUND = "payment.refund.command";
    
    public static final String TOPIC_NOTIFICATION_SEND = "notification.send.command";
}

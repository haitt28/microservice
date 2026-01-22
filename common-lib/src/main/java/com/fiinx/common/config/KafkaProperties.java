package com.fiinx.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * BEST PRACTICE #9: Cấu hình Kafka tập trung (Externalized Configuration).
 * 
 * - Binding cấu hình kiểu type-safe.
 * - Dễ dàng ghi đè (override) theo từng môi trường (environment).
 */
@Data
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
    
    /**
     * Danh sách các Kafka bootstrap servers.
     */
    private String bootstrapServers = "localhost:9092";
    
    /**
     * Tiền tố (prefix) cho Consumer group ID.
     */
    private String consumerGroupPrefix = "fiinx";
    
    /**
     * Các cài đặt cho Producer.
     */
    private Producer producer = new Producer();
    
    /**
     * Các cài đặt cho Consumer.
     */
    private Consumer consumer = new Consumer();
    
    /**
     * Cấu hình cho các Topics.
     */
    private Map<String, TopicConfig> topics = new HashMap<>();
    
    /**
     * Cài đặt cho Dead Letter Queue (DLQ).
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
         * Tính Idempotence - đảm bảo ngữ nghĩa exactly-once.
         */
        private boolean enableIdempotence = true;
        
        /**
         * Số lượng yêu cầu đang chờ xử lý (in-flight requests) tối đa trên mỗi kết nối.
         * Nên <= 5 khi enable idempotence.
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
         * Mức độ đồng thời (số lượng consumer threads).
         */
        private int concurrency = 3;
        
        /**
         * Cấu hình cơ chế Retry.
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
    // BEST PRACTICE: Tập trung khai báo hằng số tên Topic.
    
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

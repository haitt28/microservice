package com.fiinx.order.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * BEST PRACTICE #25: Cấu hình Kafka (Kafka Configuration).
 * 
 * - Manual acknowledgment phục vụ ngữ nghĩa exactly-once.
 * - Dead Letter Queue (DLQ) để xử lý các tin nhắn lỗi.
 * - Retry với cơ chế backoff.
 * - Idempotent producer đảm bảo không trùng lặp dữ liệu.
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    
    private final KafkaProperties kafkaProperties;
    
    // ==================== Producer Configuration ====================
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties(null));
        
        // Idempotent producer đảm bảo ngữ nghĩa exactly-once (không trùng lặp)
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        
        // Cơ chế nén dữ liệu (Compression)
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        // Gom nhóm tin nhắn (Batching) để tối ưu hiệu năng (Performance)
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        
        // Các bộ Serializers (Chuyển đổi dữ liệu sang Byte)
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    // ==================== Consumer Configuration ====================
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        
        // Cơ chế Manual Commit (Xác nhận thủ công)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        // Các cài đặt cho Consumer
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        
        // Các bộ Deserializers (Chuyển đổi dữ liệu từ Byte sang Object)
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.fiinx.common.event.*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
        
        return new DefaultKafkaConsumerFactory<>(config);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {
        
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory());
        
        // Chế độ xác nhận thủ công (Manual acknowledgment)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        
        // Mức độ xử lý đồng thời (Concurrency)
        factory.setConcurrency(3);
        
        // Xử lý lỗi kết hợp với Dead Letter Queue (DLQ)
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(1000L, 3)  // 3 retries, 1 second apart
        );
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }
}

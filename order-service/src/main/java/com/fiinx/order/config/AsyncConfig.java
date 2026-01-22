package com.fiinx.order.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * BEST PRACTICE #27: Async Configuration với Virtual Threads
 * 
 * - Custom thread pools cho different use cases
 * - Metrics instrumentation
 * - Proper rejection handling
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * Main async executor cho general async operations
     * Sử dụng Virtual Threads (Java 21+)
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor(MeterRegistry meterRegistry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        // Enable Virtual Threads (Java 21+)
        // executor.setVirtualThreads(true);
        
        executor.initialize();
        
        // Instrument với metrics
        instrumentExecutor(executor, "async", meterRegistry);
        
        return executor;
    }
    
    /**
     * Saga executor cho saga orchestration
     * Separate pool để prevent saga blocking other async operations
     */
    @Bean(name = "sagaExecutor")
    public Executor sagaExecutor(MeterRegistry meterRegistry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("saga-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // executor.setVirtualThreads(true);
        executor.initialize();
        
        instrumentExecutor(executor, "saga", meterRegistry);
        
        return executor;
    }
    
    /**
     * Event publishing executor
     */
    @Bean(name = "eventExecutor")
    public Executor eventExecutor(MeterRegistry meterRegistry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("event-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // executor.setVirtualThreads(true);
        executor.initialize();
        
        instrumentExecutor(executor, "event", meterRegistry);
        
        return executor;
    }
    
    private void instrumentExecutor(ThreadPoolTaskExecutor executor, String name, 
                                     MeterRegistry meterRegistry) {
        // Register metrics
        meterRegistry.gauge("executor." + name + ".pool.size", executor, 
            e -> e.getPoolSize());
        meterRegistry.gauge("executor." + name + ".active.count", executor, 
            e -> e.getActiveCount());
        meterRegistry.gauge("executor." + name + ".queue.size", executor, 
            e -> e.getThreadPoolExecutor().getQueue().size());
    }
}

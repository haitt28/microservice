package com.fiinx.order.infrastructure.lock;

import com.fiinx.common.annotation.DistributedLock;
import com.fiinx.common.exception.DistributedLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * BEST PRACTICE #22: Aspect cho Khóa phân tán (Distributed Lock Aspect).
 * 
 * Sử dụng AOP để triển khai logic cho annotation @DistributedLock.
 * 
 * Các tính năng:
 * - Parsing biểu thức SpEL để tạo dynamic keys.
 * - Cấu hình được thời gian chờ (wait time) và thời gian thuê (lease time).
 * - Tự động giải phóng (release) Lock.
 * - Chế độ Fail-fast để chống lỗi double-click.
 */
@Slf4j
@Aspect
@Component
@Order(1)  // Chạy trước @Transactional
@RequiredArgsConstructor
public class DistributedLockAspect {
    
    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        
        // Parse lock key từ biểu thức SpEL
        String lockKey = buildLockKey(joinPoint, distributedLock);
        
        log.debug("Attempting to acquire lock: {}", lockKey);
        
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        
        try {
            // Thử chiếm (acquire) Lock
            acquired = lock.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
            );
            
            if (!acquired) {
                log.warn("Failed to acquire lock: {}. Operation may already be in progress.", lockKey);
                throw new DistributedLockException(lockKey);
            }
            
            log.debug("Lock acquired: {}", lockKey);
            
            // Thực thi phương thức gốc (business logic)
            return joinPoint.proceed();
            
        } finally {
            // Giải phóng (release) Lock nếu đã chiếm thành công trước đó
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("Lock released: {}", lockKey);
            }
        }
    }
    
    /**
     * Tạo lock key từ biểu thức SpEL.
     */
    private String buildLockKey(ProceedingJoinPoint joinPoint, DistributedLock annotation) {
        String keyExpression = annotation.key();
        String prefix = annotation.prefix();
        
        // Tạo evaluation context với các tham số của phương thức
        EvaluationContext context = createEvaluationContext(joinPoint);
        
        // Parse biểu thức SpEL
        String key = expressionParser.parseExpression(keyExpression)
            .getValue(context, String.class);
        
        return prefix + key;
    }
    
    /**
     * Tạo SpEL evaluation context chứa các tham số của phương thức.
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // Thêm các tham số của phương thức vào context
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), args[i]);
        }
        
        return context;
    }
}

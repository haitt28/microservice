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
 * BEST PRACTICE #22: Distributed Lock Aspect
 * 
 * AOP aspect để implement @DistributedLock annotation
 * 
 * Features:
 * - SpEL expression parsing cho dynamic keys
 * - Configurable wait/lease times
 * - Automatic lock release
 * - Fail-fast mode cho double-click prevention
 */
@Slf4j
@Aspect
@Component
@Order(1)  // Run before @Transactional
@RequiredArgsConstructor
public class DistributedLockAspect {
    
    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        
        // Parse lock key from SpEL expression
        String lockKey = buildLockKey(joinPoint, distributedLock);
        
        log.debug("Attempting to acquire lock: {}", lockKey);
        
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        
        try {
            // Try to acquire lock
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
            
            // Execute the method
            return joinPoint.proceed();
            
        } finally {
            // Release lock if we acquired it
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("Lock released: {}", lockKey);
            }
        }
    }
    
    /**
     * Build lock key from SpEL expression
     */
    private String buildLockKey(ProceedingJoinPoint joinPoint, DistributedLock annotation) {
        String keyExpression = annotation.key();
        String prefix = annotation.prefix();
        
        // Create evaluation context with method parameters
        EvaluationContext context = createEvaluationContext(joinPoint);
        
        // Parse SpEL expression
        String key = expressionParser.parseExpression(keyExpression)
            .getValue(context, String.class);
        
        return prefix + key;
    }
    
    /**
     * Create SpEL evaluation context with method parameters
     */
    private EvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // Add method parameters to context
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), args[i]);
        }
        
        return context;
    }
}

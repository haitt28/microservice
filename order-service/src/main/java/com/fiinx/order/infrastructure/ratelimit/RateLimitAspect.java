package com.fiinx.order.infrastructure.ratelimit;

import com.fiinx.common.annotation.RateLimited;
import com.fiinx.common.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.redisson.client.codec.StringCodec;

/**
 * BEST PRACTICE #23: Rate Limiting Aspect
 * 
 * Sliding Window Rate Limiting với Redis.
 * Giải thích cho Senior: Chúng ta dùng AOP để tách biệt logic Rate Limit khỏi Business Logic.
 * 
 * Algorithm:
 * 1. Key format: ratelimit:{key}:{window_timestamp}
 * 2. Dùng Lua script để đảm bảo tính Atomic (không bị tranh chấp dữ liệu).
 * 3. Count requests trong current window.
 * 4. Reject nếu vượt limit.
 */
@Slf4j
@Aspect
@Component
@Order(0)  // Chạy trước các Aspect khác để chặn request sớm nhất có thể
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    // Lua script giúp thực hiện nhiều lệnh Redis trong 1 transaction duy nhất (Atomic)
    // Tránh lỗi Race Condition khi 2 request đến cùng lúc
    private static final String RATE_LIMIT_SCRIPT = """
        local key = KEYS[1]
        local limit = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        
        local current = redis.call('INCR', key)
        
        if current == 1 then
            -- Nếu là request đầu tiên, set thời gian sống cho key
            redis.call('EXPIRE', key, window)
        end
        
        if current > limit then
            return 0 -- Vượt hạn mức
        end
        
        return 1 -- Hợp lệ
        """;
    
    @Around("@annotation(rateLimited)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        
        String rateLimitKey = buildRateLimitKey(joinPoint, rateLimited);
        int limit = rateLimited.limit();
        long windowSeconds = rateLimited.timeUnit().toSeconds(rateLimited.window());
        
        log.debug("Checking rate limit for key: {} (limit: {}/{}s)", 
            rateLimitKey, limit, windowSeconds);
        
        // Thực thi Lua script
        // QUAN TRỌNG: Dùng StringCodec để đảm bảo ARGV được cast sang số trong Lua (tonumber)
        RScript script = redissonClient.getScript(StringCodec.INSTANCE);
        Long result = script.eval(
            RScript.Mode.READ_WRITE,
            RATE_LIMIT_SCRIPT,
            RScript.ReturnType.INTEGER,
            Arrays.asList(rateLimitKey),
            String.valueOf(limit), String.valueOf(windowSeconds)
        );
        
        Long allowed = result;
        
        if (allowed == null || allowed == 0) {
            log.warn("Rate limit exceeded for key: {}", rateLimitKey);
            throw new RateLimitExceededException(windowSeconds);
        }
        
        return joinPoint.proceed();
    }
    
    private String buildRateLimitKey(ProceedingJoinPoint joinPoint, RateLimited annotation) {
        StringBuilder keyBuilder = new StringBuilder("ratelimit:");
        
        // Use user ID if perUser is set
        if (annotation.perUser()) {
            String userId = getCurrentUserId();
            keyBuilder.append("user:").append(userId).append(":");
        }
        
        // Parse SpEL expression if provided
        if (!annotation.key().isEmpty()) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), args[i]);
            }
            
            String parsedKey = expressionParser.parseExpression(annotation.key())
                .getValue(context, String.class);
            keyBuilder.append(parsedKey);
        } else {
            // Default: use method signature
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            keyBuilder.append(signature.getDeclaringType().getSimpleName())
                .append(":")
                .append(signature.getName());
        }
        
        // Add time window for sliding window
        long windowSeconds = annotation.timeUnit().toSeconds(annotation.window());
        long windowTimestamp = System.currentTimeMillis() / (windowSeconds * 1000);
        keyBuilder.append(":").append(windowTimestamp);
        
        return keyBuilder.toString();
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}

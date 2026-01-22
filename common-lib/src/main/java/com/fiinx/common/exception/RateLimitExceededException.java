package com.fiinx.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when rate limit is exceeded
 */
public class RateLimitExceededException extends BusinessException {
    
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super("RATE_LIMIT_EXCEEDED", message, HttpStatus.TOO_MANY_REQUESTS);
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public RateLimitExceededException(long retryAfterSeconds) {
        this("Rate limit exceeded. Please try again later.", retryAfterSeconds);
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}

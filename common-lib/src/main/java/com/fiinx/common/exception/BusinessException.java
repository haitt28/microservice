package com.fiinx.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * BEST PRACTICE #3: Custom Exception Hierarchy
 * 
 * - Base class cho tất cả business exceptions
 * - Chứa error code để client có thể handle programmatically
 * - Map tới HTTP status code phù hợp
 * 
 * Exception Hierarchy:
 * BusinessException
 * ├── ResourceNotFoundException (404)
 * ├── ResourceConflictException (409)
 * ├── ValidationException (400)
 * ├── RateLimitExceededException (429)
 * ├── DistributedLockException (409)
 * └── SagaExecutionException (500)
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final transient Object[] args;  // For i18n message formatting
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = new Object[0];
    }
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }
    
    public BusinessException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = new Object[0];
    }
}

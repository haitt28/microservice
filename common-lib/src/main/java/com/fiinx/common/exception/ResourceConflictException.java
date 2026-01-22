package com.fiinx.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when there's a conflict with existing resource state
 * Example: Duplicate order ID, concurrent modification
 */
public class ResourceConflictException extends BusinessException {
    
    public ResourceConflictException(String message) {
        super("RESOURCE_CONFLICT", message, HttpStatus.CONFLICT);
    }
    
    public ResourceConflictException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}

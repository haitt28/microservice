package com.fiinx.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when distributed lock cannot be acquired
 * Typically happens on double-click scenarios
 */
public class DistributedLockException extends BusinessException {
    
    public DistributedLockException(String lockKey) {
        super(
            "LOCK_ACQUISITION_FAILED",
            String.format("Could not acquire lock for key: %s. Operation may already be in progress.", lockKey),
            HttpStatus.CONFLICT
        );
    }
    
    public DistributedLockException(String lockKey, String message) {
        super("LOCK_ACQUISITION_FAILED", message, HttpStatus.CONFLICT, lockKey);
    }
}

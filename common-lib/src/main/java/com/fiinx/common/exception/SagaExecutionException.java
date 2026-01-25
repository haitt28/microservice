package com.fiinx.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ được ném ra khi một bước (Step) trong Saga thất bại và kích hoạt cơ chế bồi hoàn (Compensation).
 */
public class SagaExecutionException extends BusinessException {
    
    private final String sagaId;
    private final String failedStep;
    
    public SagaExecutionException(String sagaId, String failedStep, String message) {
        super(
            "SAGA_EXECUTION_FAILED",
            message,
            HttpStatus.INTERNAL_SERVER_ERROR,
            sagaId, failedStep
        );
        this.sagaId = sagaId;
        this.failedStep = failedStep;
    }
    
    public SagaExecutionException(String sagaId, String failedStep, String message, Throwable cause) {
        super("SAGA_EXECUTION_FAILED", message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
        this.sagaId = sagaId;
        this.failedStep = failedStep;
    }
    
    public String getSagaId() {
        return sagaId;
    }
    
    public String getFailedStep() {
        return failedStep;
    }
}

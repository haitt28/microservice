package com.fiinx.common.exception;

import com.fiinx.common.dto.ApiResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for Security-related exceptions.
 * Only loaded when Spring Security is on the classpath.
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(AccessDeniedException.class)
public class SecurityExceptionHandler {

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private Tracer tracer;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse("ACCESS_DENIED", "You don't have permission to access this resource"));
    }

    private ApiResponse<Void> buildErrorResponse(String code, String message) {
        ApiResponse<Void> response = ApiResponse.error(code, message);
        
        // Add trace ID if available
        if (tracer != null) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null && response.getError() != null) {
                response.getError().setTraceId(currentSpan.context().traceId());
            }
        }
        
        return response;
    }
}

package com.fiinx.common.exception;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.common.dto.ApiResponse.FieldError;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Optional;

/**
 * Bộ xử lý lỗi tập trung (Centralized Error Handling).
 * Senior Note: Mọi lỗi trong hệ thống đều phải đi qua đây để đảm bảo:
 * 1. Log được Trace ID (giúp bạn tìm lỗi trên Jaeger cực nhanh giữa hàng nghìn log).
 * 2. Phản hồi API luôn có cấu trúc đồng nhất, giúp Frontend xử lý dễ dàng hơn.
 * 3. Không bao giờ để lộ thông tin nhạy cảm của hệ thống ra ngoài.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private Tracer tracer;
    
    // ==================== Business Exceptions ====================
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitException(
            RateLimitExceededException ex, HttpServletRequest request) {
        
        log.warn("Rate limit exceeded for request: {}", request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage()));
    }
    
    // ==================== Validation Exceptions ====================
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();
        
        log.warn("Validation failed: {} errors", fieldErrors.size());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_FAILED", "Validation failed", fieldErrors));
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("BINDING_FAILED", "Request binding failed", fieldErrors));
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        
        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(),
                Optional.ofNullable(ex.getRequiredType())
                        .map(Class::getSimpleName)
                        .orElse("unknown"));
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse("TYPE_MISMATCH", message));
    }
    
    // ==================== Security Exceptions moved to SecurityExceptionHandler ====================
    
    // ==================== Catch-all Handler ====================
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        // BEST PRACTICE: Log toàn bộ stack trace nhưng không bao giờ để lộ ra phía client.
        log.error("Unexpected error for request {} {}: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("INTERNAL_ERROR", "An unexpected error occurred. Please try again later."));
    }
    
    // ==================== Helper Methods ====================
    
    private ApiResponse<Void> buildErrorResponse(String code, String message) {
        ApiResponse<Void> response = ApiResponse.error(code, message);
        
        // Gắn Trace ID vào phản hồi (response) nếu có. 
        // Khi client báo lỗi, họ chỉ cần cung cấp Trace ID này để bạn tìm lỗi nhanh chóng.
        if (tracer != null) {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan != null && response.getError() != null) {
                response.getError().setTraceId(currentSpan.context().traceId());
            }
        }
        
        return response;
    }
}

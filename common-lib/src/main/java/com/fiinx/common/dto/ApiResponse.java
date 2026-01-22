package com.fiinx.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Senior Note: Wrapper chuẩn cho phản hồi API (Unified API Response).
 * 
 * - Đảm bảo định dạng Response đồng nhất giữa tất cả các Microservices.
 * - Giúp Client (Frontend/Mobile) nắm rõ cấu trúc dữ liệu trả về.
 * - Dễ dàng xử lý lỗi tập trung tại Frontend.
 * 
 * @param <T> Kiểu dữ liệu (Payload) của Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Không serialize null fields
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates if the request was successful")
    private boolean success;
    
    @Schema(description = "Response data payload")
    private T data;
    
    @Schema(description = "Error information if request failed")
    private ErrorInfo error;
    
    @Schema(description = "Additional metadata")
    private Meta meta;
    
    // ==================== Factory Methods ====================
    // BEST PRACTICE: Static factory methods cho clean API
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(Meta.builder().timestamp(Instant.now()).build())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .meta(Meta.builder()
                        .timestamp(Instant.now())
                        .message(message)
                        .build())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .build())
                .meta(Meta.builder().timestamp(Instant.now()).build())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message, List<FieldError> fieldErrors) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorInfo.builder()
                        .code(code)
                        .message(message)
                        .fieldErrors(fieldErrors)
                        .build())
                .meta(Meta.builder().timestamp(Instant.now()).build())
                .build();
    }
    
    // ==================== Nested Classes ====================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error details")
    public static class ErrorInfo {
        
        @Schema(description = "Error code for programmatic handling", example = "ORDER_NOT_FOUND")
        private String code;
        
        @Schema(description = "Human-readable error message")
        private String message;
        
        @Schema(description = "Correlation ID for tracing")
        private String traceId;
        
        @Schema(description = "Field-level validation errors")
        private List<FieldError> fieldErrors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field validation error")
    public static class FieldError {
        
        @Schema(description = "Field name that has error", example = "email")
        private String field;
        
        @Schema(description = "Error message for this field", example = "must be a valid email")
        private String message;
        
        @Schema(description = "Rejected value", example = "invalid-email")
        private Object rejectedValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Response metadata")
    public static class Meta {
        
        @Schema(description = "Response timestamp")
        private Instant timestamp;
        
        @Schema(description = "Optional success message")
        private String message;
        
        @Schema(description = "API version")
        private String version;
        
        @Schema(description = "Request processing time in milliseconds")
        private Long processingTimeMs;
    }
}

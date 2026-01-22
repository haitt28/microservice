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
@Schema(description = "Lớp wrapper chuẩn cho phản hồi API (Unified API Response)")
public class ApiResponse<T> {
    
    @Schema(description = "Cho biết yêu cầu có thành công hay không")
    private boolean success;
    
    @Schema(description = "Dữ liệu phản hồi (payload)")
    private T data;
    
    @Schema(description = "Thông tin lỗi nếu yêu cầu thất bại")
    private ErrorInfo error;
    
    @Schema(description = "Thông tin metadata bổ sung")
    private Meta meta;
    
    // ==================== Factory Methods ====================
    // BEST PRACTICE: Các static factory methods để tạo API sạch hơn (clean API)
    
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
    @Schema(description = "Chi tiết thông tin lỗi")
    public static class ErrorInfo {
        
        @Schema(description = "Mã lỗi để xử lý bằng code", example = "ORDER_NOT_FOUND")
        private String code;
        
        @Schema(description = "Thông báo lỗi thân thiện cho con người")
        private String message;
        
        @Schema(description = "Trace ID phục vụ việc truy vết (tracing)")
        private String traceId;
        
        @Schema(description = "Các lỗi validation cụ thể ở mức trường (field)")
        private List<FieldError> fieldErrors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Lỗi validation của một trường dữ liệu")
    public static class FieldError {
        
        @Schema(description = "Tên trường gặp lỗi", example = "email")
        private String field;
        
        @Schema(description = "Thông báo lỗi cho trường này", example = "must be a valid email")
        private String message;
        
        @Schema(description = "Giá trị bị từ chối", example = "invalid-email")
        private Object rejectedValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metadata của phản hồi")
    public static class Meta {
        
        @Schema(description = "Thời điểm phản hồi")
        private Instant timestamp;
        
        @Schema(description = "Thông báo thành công tùy chọn")
        private String message;
        
        @Schema(description = "Phiên bản API")
        private String version;
        
        @Schema(description = "Thời gian xử lý yêu cầu (ms)")
        private Long processingTimeMs;
    }
}

package com.fiinx.order.api;

import com.fiinx.common.annotation.RateLimited;
import com.fiinx.common.dto.ApiResponse;
import com.fiinx.common.dto.PageResponse;
import com.fiinx.order.application.dto.CreateOrderRequest;
import com.fiinx.order.application.dto.OrderResponse;
import com.fiinx.order.application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * BEST PRACTICE #24: REST Controller (Bộ điều khiển REST)
 * 
 * - Tài liệu API rõ ràng với OpenAPI.
 * - Áp dụng Rate limiting cho các endpoint công khai.
 * - Bảo mật với phân quyền ở mức phương thức (method-level authorization).
 * - Sử dụng các mã phản hồi HTTP (HTTP status codes) phù hợp.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Đơn hàng", description = "API quản lý đơn hàng")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Create new order
     */
    @PostMapping
    @RateLimited(key = "'order:create:' + #jwt.subject", limit = 10, window = 1, timeUnit = TimeUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Tạo đơn hàng", description = "Tạo một đơn hàng mới cho người dùng đã xác thực")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        log.info("Create order request from user: {}", jwt.getSubject());
        
        // Override customerId with authenticated user
        request.setCustomerId(jwt.getSubject());
        if (request.getCustomerEmail() == null) {
            request.setCustomerEmail(jwt.getClaimAsString("email"));
        }
        
        OrderResponse order = orderService.createOrder(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Đơn hàng đã được tạo thành công"));
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy đơn hàng", description = "Lấy thông tin đơn hàng theo ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        
        OrderResponse order = orderService.getOrder(orderId);
        
        // Verify ownership (unless admin)
        if (!jwt.getClaimAsStringList("roles").contains("ADMIN") &&
            !order.getCustomerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("ACCESS_DENIED", "Bạn không có quyền truy cập vào đơn hàng này"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/by-number/{orderNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy đơn hàng theo mã số", description = "Lấy thông tin đơn hàng theo mã đơn hàng (order number)")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal Jwt jwt) {
        
        OrderResponse order = orderService.getOrderByNumber(orderNumber);
        
        // Verify ownership
        if (!jwt.getClaimAsStringList("roles").contains("ADMIN") &&
            !order.getCustomerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this order"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Get current user's orders
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy đơn hàng của tôi", description = "Lấy danh sách đơn hàng phân trang của người dùng hiện tại")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        PageResponse<OrderResponse> orders = orderService.getCustomerOrders(jwt.getSubject(), pageable);
        
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    /**
     * Cancel order
     */
    @PostMapping("/{orderNumber}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Hủy đơn hàng", description = "Hủy một đơn hàng hiện có")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderNumber,
            @RequestParam(defaultValue = "Khách hàng yêu cầu hủy đơn") String reason,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Verify ownership first
        OrderResponse existing = orderService.getOrderByNumber(orderNumber);
        if (!jwt.getClaimAsStringList("roles").contains("ADMIN") &&
            !existing.getCustomerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this order"));
        }
        
        OrderResponse order = orderService.cancelOrder(orderNumber, reason);
        
        return ResponseEntity.ok(ApiResponse.success(order, "Đơn hàng đã được hủy thành công"));
    }
    
    /**
     * Get order tracking timeline
     */
    @GetMapping("/{orderId}/tracking")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy lịch trình đơn hàng", description = "Lấy chi tiết lịch trình theo dõi đơn hàng (tracking timeline)")
    public ResponseEntity<ApiResponse<java.util.List<com.fiinx.order.application.dto.OrderTimelineResponse>>> getOrderTracking(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderTimeline(orderId)));
    }

    /**
     * Get order invoice (Mock)
     */
    @GetMapping("/{orderId}/invoice")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Lấy hóa đơn đơn hàng", description = "Tạo và trả về hóa đơn đơn hàng (URL PDF giả lập)")
    public ResponseEntity<ApiResponse<String>> getOrderInvoice(@PathVariable UUID orderId) {
        // Mock generation
        String mockInvoiceUrl = "https://fiinx.com/invoices/INV-" + orderId + ".pdf";
        return ResponseEntity.ok(ApiResponse.success(mockInvoiceUrl, "Hóa đơn đã được tạo"));
    }

    // ==================== Các Endpoint Quản Trị (Admin Endpoints) ====================
    
    /**
     * Get all orders (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liệt kê tất cả đơn hàng (Admin)", description = "Lấy danh sách tất cả các đơn hàng phân trang với bộ lọc")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // For simplicity, we use the existing find all but could add complex filters
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(pageable)));
    }

    /**
     * Get orders by customer ID (Admin only)
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy đơn hàng của khách hàng (Admin)", description = "Lấy danh sách đơn hàng cho một khách hàng cụ thể")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getCustomerOrders(
            @PathVariable String customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        PageResponse<OrderResponse> orders = orderService.getCustomerOrders(customerId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}

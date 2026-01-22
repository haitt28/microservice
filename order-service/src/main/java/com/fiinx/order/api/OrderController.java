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
 * BEST PRACTICE #24: REST Controller
 * 
 * - Clear API documentation với OpenAPI
 * - Rate limiting cho public endpoints
 * - Security với method-level authorization
 * - Proper HTTP status codes
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API")
@SecurityRequirement(name = "bearer-jwt")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Create new order
     */
    @PostMapping
    @RateLimited(key = "'order:create:' + #jwt.subject", limit = 10, window = 1, timeUnit = TimeUnit.MINUTES)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create order", description = "Create a new order for the authenticated user")
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
                .body(ApiResponse.success(order, "Order created successfully"));
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get order", description = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        
        OrderResponse order = orderService.getOrder(orderId);
        
        // Verify ownership (unless admin)
        if (!jwt.getClaimAsStringList("roles").contains("ADMIN") &&
            !order.getCustomerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this order"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/by-number/{orderNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get order by number", description = "Get order by order number")
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
    @Operation(summary = "Get my orders", description = "Get paginated list of current user's orders")
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
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderNumber,
            @RequestParam(defaultValue = "Customer requested cancellation") String reason,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Verify ownership first
        OrderResponse existing = orderService.getOrderByNumber(orderNumber);
        if (!jwt.getClaimAsStringList("roles").contains("ADMIN") &&
            !existing.getCustomerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this order"));
        }
        
        OrderResponse order = orderService.cancelOrder(orderNumber, reason);
        
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"));
    }
    
    // ==================== Admin Endpoints ====================
    
    /**
     * Get orders by customer ID (Admin only)
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get customer orders (Admin)", description = "Get orders for a specific customer")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getCustomerOrders(
            @PathVariable String customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        PageResponse<OrderResponse> orders = orderService.getCustomerOrders(customerId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}

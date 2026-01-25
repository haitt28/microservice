package com.fiinx.cart.api;

import com.fiinx.cart.application.service.CartService;
import com.fiinx.cart.domain.model.Cart;
import com.fiinx.cart.domain.model.CartItem;
import com.fiinx.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Senior Note: Cart Controller (Bộ điều khiển Giỏ hàng)
 * 
 * - Giỏ hàng dựa trên Session cho khách hàng vãng lai (Guest).
 * - Giỏ hàng dựa trên User cho người dùng đã đăng nhập.
 * - Tự động gộp (Merge) giỏ hàng khi người dùng đăng nhập.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Giỏ hàng", description = "API quản lý giỏ hàng")
public class CartController {
    
    private final CartService cartService;
    
    /**
     * Get current cart
     */
    @GetMapping
    @Operation(summary = "Lấy giỏ hàng", description = "Lấy thông tin giỏ hàng hiện tại của người dùng")
    public ResponseEntity<ApiResponse<Cart>> getCart(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.getCart(cartId);
        
        return ResponseEntity.ok(ApiResponse.success(cart));
    }
    
    /**
     * Add item to cart
     */
    @PostMapping("/items")
    @Operation(summary = "Thêm sản phẩm", description = "Thêm một sản phẩm vào giỏ hàng")
    public ResponseEntity<ApiResponse<Cart>> addItem(
            @RequestBody CartItem item,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.addItem(cartId, item);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Sản phẩm đã được thêm vào giỏ hàng"));
    }
    
    /**
     * Update item quantity
     */
    @PutMapping("/items/{productId}")
    @Operation(summary = "Cập nhật số lượng", description = "Cập nhật số lượng sản phẩm trong giỏ hàng")
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(
            @PathVariable UUID productId,
            @RequestParam int quantity,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.updateItemQuantity(cartId, productId, quantity);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Số lượng đã được cập nhật"));
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Xóa sản phẩm", description = "Xóa sản phẩm khỏi giỏ hàng")
    public ResponseEntity<ApiResponse<Cart>> removeItem(
            @PathVariable UUID productId,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.removeItem(cartId, productId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Sản phẩm đã được xóa khỏi giỏ hàng"));
    }
    
    /**
     * Apply coupon
     */
    @PostMapping("/coupon")
    @Operation(summary = "Áp dụng mã giảm giá", description = "Áp dụng mã giảm giá (coupon) cho giỏ hàng")
    public ResponseEntity<ApiResponse<Cart>> applyCoupon(
            @RequestParam String code,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.applyCoupon(cartId, code);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Mã giảm giá đã được áp dụng"));
    }
    
    /**
     * Remove coupon
     */
    @DeleteMapping("/coupon")
    @Operation(summary = "Gỡ bỏ mã giảm giá", description = "Gỡ bỏ mã giảm giá đã áp dụng")
    public ResponseEntity<ApiResponse<Cart>> removeCoupon(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.removeCoupon(cartId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Mã giảm giá đã được gỡ bỏ"));
    }
    
    /**
     * Clear cart
     */
    @DeleteMapping
    @Operation(summary = "Xóa sạch giỏ hàng", description = "Xóa tất cả sản phẩm khỏi giỏ hàng")
    public ResponseEntity<ApiResponse<Cart>> clearCart(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.clearCart(cartId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Giỏ hàng đã được xóa sạch"));
    }
    
    /**
     * Merge guest cart (called after login)
     */
    @PostMapping("/merge")
    @Operation(summary = "Gộp giỏ hàng", description = "Gộp giỏ hàng từ session vào giỏ hàng của user sau khi đăng nhập")
    public ResponseEntity<ApiResponse<Cart>> mergeCart(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String userId = jwt.getSubject();
        String sessionId = session.getId();
        
        Cart cart = cartService.mergeGuestCart(sessionId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Giỏ hàng đã được gộp thành công"));
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Get cart ID (user ID nếu authenticated, session ID nếu guest)
     */
    private String getCartId(Jwt jwt, HttpSession session) {
        if (jwt != null) {
            // Authenticated user
            return "user:" + jwt.getSubject();
        } else {
            // Guest user
            return "session:" + session.getId();
        }
    }
}

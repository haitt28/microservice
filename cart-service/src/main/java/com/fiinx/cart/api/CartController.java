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
 * Senior Note: Cart Controller
 * 
 * - Session-based cart cho guest users
 * - User-based cart cho authenticated users
 * - Auto-merge khi login
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart API")
public class CartController {
    
    private final CartService cartService;
    
    /**
     * Get current cart
     */
    @GetMapping
    @Operation(summary = "Get cart", description = "Get current user's cart")
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
    @Operation(summary = "Add item", description = "Add item to cart")
    public ResponseEntity<ApiResponse<Cart>> addItem(
            @RequestBody CartItem item,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.addItem(cartId, item);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart"));
    }
    
    /**
     * Update item quantity
     */
    @PutMapping("/items/{productId}")
    @Operation(summary = "Update quantity", description = "Update item quantity in cart")
    public ResponseEntity<ApiResponse<Cart>> updateQuantity(
            @PathVariable UUID productId,
            @RequestParam int quantity,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.updateItemQuantity(cartId, productId, quantity);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Quantity updated"));
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item", description = "Remove item from cart")
    public ResponseEntity<ApiResponse<Cart>> removeItem(
            @PathVariable UUID productId,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.removeItem(cartId, productId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removed"));
    }
    
    /**
     * Apply coupon
     */
    @PostMapping("/coupon")
    @Operation(summary = "Apply coupon", description = "Apply discount coupon")
    public ResponseEntity<ApiResponse<Cart>> applyCoupon(
            @RequestParam String code,
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.applyCoupon(cartId, code);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Coupon applied"));
    }
    
    /**
     * Remove coupon
     */
    @DeleteMapping("/coupon")
    @Operation(summary = "Remove coupon", description = "Remove applied coupon")
    public ResponseEntity<ApiResponse<Cart>> removeCoupon(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.removeCoupon(cartId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Coupon removed"));
    }
    
    /**
     * Clear cart
     */
    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from cart")
    public ResponseEntity<ApiResponse<Cart>> clearCart(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String cartId = getCartId(jwt, session);
        Cart cart = cartService.clearCart(cartId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart cleared"));
    }
    
    /**
     * Merge guest cart (called after login)
     */
    @PostMapping("/merge")
    @Operation(summary = "Merge cart", description = "Merge session cart to user cart after login")
    public ResponseEntity<ApiResponse<Cart>> mergeCart(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session
    ) {
        String userId = jwt.getSubject();
        String sessionId = session.getId();
        
        Cart cart = cartService.mergeGuestCart(sessionId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart merged"));
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

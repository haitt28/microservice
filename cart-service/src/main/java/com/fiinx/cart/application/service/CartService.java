package com.fiinx.cart.application.service;

import com.fiinx.cart.domain.model.Cart;
import com.fiinx.cart.domain.model.CartItem;
import com.fiinx.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Senior Note: Cart Service với Redis storage
 * 
 * - Session-based cart cho guest
 * - User-based cart cho authenticated users
 * - Auto-merge khi guest login
 * - TTL 24h cho inactive carts
 * 
 * BEST PRACTICE:
 * - Redis cho fast read/write
 * - Denormalize product info để không phụ thuộc Product Service
 * - Validation trước khi add (stock check qua Product Service)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    
    private final RedissonClient redissonClient;
    // private final ProductServiceClient productServiceClient; // TODO: implement WebClient
    // private final PromotionServiceClient promotionServiceClient; // TODO
    
    private static final String CART_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofHours(24);
    
    /**
     * Get cart by ID (create new if not exists)
     */
    public Cart getCart(String cartId) {
        RBucket<Cart> bucket = redissonClient.getBucket(CART_PREFIX + cartId);
        Cart cart = bucket.get();
        
        if (cart == null) {
            cart = Cart.builder()
                .id(cartId)
                .items(new java.util.ArrayList<>())
                .subtotal(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
            
            saveCart(cart);
        }
        
        return cart;
    }
    
    /**
     * Get cart by user ID
     */
    public Cart getCartByUserId(String userId) {
        String cartId = "user:" + userId;
        return getCart(cartId);
    }
    
    /**
     * Get cart by session ID
     */
    public Cart getCartBySessionId(String sessionId) {
        String cartId = "session:" + sessionId;
        return getCart(cartId);
    }
    
    /**
     * Add item to cart
     */
    public Cart addItem(String cartId, CartItem item) {
        log.info("Adding item to cart: cartId={}, productId={}", cartId, item.getProductId());
        
        // TODO: Validate product exists and in stock (call Product Service)
        // ProductInfo product = productServiceClient.getProduct(item.getProductId());
        // if (!product.isInStock()) {
        //     throw new BusinessException("Product out of stock");
        // }
        
        Cart cart = getCart(cartId);
        item.updateTotals();
        cart.addItem(item);
        
        saveCart(cart);
        
        log.info("Item added to cart: cartId={}, totalItems={}", cartId, cart.getTotalItems());
        
        return cart;
    }
    
    /**
     * Remove item from cart
     */
    public Cart removeItem(String cartId, UUID productId) {
        log.info("Removing item from cart: cartId={}, productId={}", cartId, productId);
        
        Cart cart = getCart(cartId);
        cart.removeItem(productId);
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Update item quantity
     */
    public Cart updateItemQuantity(String cartId, UUID productId, int quantity) {
        log.info("Updating item quantity: cartId={}, productId={}, quantity={}", 
                cartId, productId, quantity);
        
        if (quantity <= 0) {
            return removeItem(cartId, productId);
        }
        
        Cart cart = getCart(cartId);
        cart.updateItemQuantity(productId, quantity);
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Clear cart
     */
    public Cart clearCart(String cartId) {
        log.info("Clearing cart: cartId={}", cartId);
        
        Cart cart = getCart(cartId);
        cart.clear();
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Apply coupon code
     */
    public Cart applyCoupon(String cartId, String couponCode) {
        log.info("Applying coupon: cartId={}, couponCode={}", cartId, couponCode);
        
        Cart cart = getCart(cartId);
        
        // TODO: Validate coupon với Promotion Service
        // CouponInfo coupon = promotionServiceClient.validateCoupon(couponCode, cart.getSubtotal());
        // BigDecimal discount = coupon.calculateDiscount(cart.getSubtotal());
        
        // Mock discount for now
        BigDecimal discount = cart.getSubtotal().multiply(BigDecimal.valueOf(0.1)); // 10% off
        
        cart.applyCoupon(couponCode, discount);
        saveCart(cart);
        
        log.info("Coupon applied: cartId={}, discount={}", cartId, discount);
        
        return cart;
    }
    
    /**
     * Remove coupon
     */
    public Cart removeCoupon(String cartId) {
        log.info("Removing coupon: cartId={}", cartId);
        
        Cart cart = getCart(cartId);
        cart.removeCoupon();
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Merge guest cart to user cart (khi login)
     */
    public Cart mergeGuestCart(String sessionId, String userId) {
        log.info("Merging guest cart to user cart: sessionId={}, userId={}", sessionId, userId);
        
        String guestCartId = "session:" + sessionId;
        String userCartId = "user:" + userId;
        
        Cart guestCart = getCart(guestCartId);
        Cart userCart = getCart(userCartId);
        
        // Merge items from guest cart to user cart
        for (CartItem item : guestCart.getItems()) {
            userCart.addItem(item);
        }
        
        // Save merged cart
        saveCart(userCart);
        
        // Delete guest cart
        deleteCart(guestCartId);
        
        log.info("Cart merged: totalItems={}", userCart.getTotalItems());
        
        return userCart;
    }
    
    /**
     * Save cart to Redis
     */
    private void saveCart(Cart cart) {
        RBucket<Cart> bucket = redissonClient.getBucket(CART_PREFIX + cart.getId());
        bucket.set(cart, CART_TTL);
    }
    
    /**
     * Delete cart
     */
    private void deleteCart(String cartId) {
        RBucket<Cart> bucket = redissonClient.getBucket(CART_PREFIX + cartId);
        bucket.delete();
    }
}

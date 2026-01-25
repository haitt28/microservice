package com.fiinx.cart.application.service;

import com.fiinx.cart.domain.model.Cart;
import com.fiinx.cart.domain.model.CartItem;
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
 * Senior Note: Cart Service với cơ chế lưu trữ Redis.
 * 
 * - Giỏ hàng dựa trên Session cho khách (Guest).
 * - Giỏ hàng dựa trên User cho người dùng đã đăng nhập.
 * - Tự động gộp (Merge) giỏ hàng khi khách đăng nhập.
 * - Thời gian sống (TTL) 24h cho các giỏ hàng không hoạt động.
 * 
 * BEST PRACTICE:
 * - Sử dụng Redis để tối ưu tốc độ đọc/ghi (read/write).
 * - Phi chuẩn hóa (Denormalize) thông tin sản phẩm để giảm phụ thuộc vào Product Service.
 * - Kiểm tra tính hợp lệ trước khi thêm (Kiểm tra tồn kho qua Product Service).
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
     * Lấy giỏ hàng theo ID (tạo mới nếu chưa tồn tại)
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
     * Lấy giỏ hàng theo User ID
     */
    public Cart getCartByUserId(String userId) {
        String cartId = "user:" + userId;
        return getCart(cartId);
    }
    
    /**
     * Lấy giỏ hàng theo Session ID
     */
    public Cart getCartBySessionId(String sessionId) {
        String cartId = "session:" + sessionId;
        return getCart(cartId);
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public Cart addItem(String cartId, CartItem item) {
        log.info("Adding item to cart: cartId={}, productId={}", cartId, item.getProductId());
        
        // TODO: Validate product exists and in stock (call Product Service)
        // ProductInfo product = productServiceClient.getProduct(item.getProductId());
        // if (!product.isInStock()) {
        //     throw new BusinessException("Sản phẩm đã hết hàng");
        // }
        
        Cart cart = getCart(cartId);
        item.updateTotals();
        cart.addItem(item);
        
        saveCart(cart);
        
        log.info("Item added to cart: cartId={}, totalItems={}", cartId, cart.getTotalItems());
        
        return cart;
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public Cart removeItem(String cartId, UUID productId) {
        log.info("Removing item from cart: cartId={}, productId={}", cartId, productId);
        
        Cart cart = getCart(cartId);
        cart.removeItem(productId);
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Cập nhật số lượng sản phẩm
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
     * Xóa sạch giỏ hàng (Clear)
     */
    public Cart clearCart(String cartId) {
        log.info("Clearing cart: cartId={}", cartId);
        
        Cart cart = getCart(cartId);
        cart.clear();
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Áp dụng mã giảm giá (Coupon)
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
     * Gỡ bỏ mã giảm giá
     */
    public Cart removeCoupon(String cartId) {
        log.info("Removing coupon: cartId={}", cartId);
        
        Cart cart = getCart(cartId);
        cart.removeCoupon();
        
        saveCart(cart);
        
        return cart;
    }
    
    /**
     * Gộp giỏ hàng của khách vào giỏ hàng của User (khi login)
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
     * Lưu giỏ hàng vào Redis
     */
    private void saveCart(Cart cart) {
        RBucket<Cart> bucket = redissonClient.getBucket(CART_PREFIX + cart.getId());
        bucket.set(cart, CART_TTL);
    }
    
    /**
     * Xóa giỏ hàng
     */
    private void deleteCart(String cartId) {
        RBucket<Cart> bucket = redissonClient.getBucket(CART_PREFIX + cartId);
        bucket.delete();
    }
}

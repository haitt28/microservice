package com.fiinx.wishlist.application.service;

import com.fiinx.wishlist.application.dto.WishlistResponse;
import com.fiinx.wishlist.application.dto.WishlistItemDto;
import com.fiinx.wishlist.domain.entity.Wishlist;
import com.fiinx.wishlist.domain.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Senior Note: Wishlist Service - Quản lý danh sách yêu thích.
 * 
 * - Cho phép người dùng lưu trữ sản phẩm quan tâm để mua sau.
 * - Quản lý theo từng User ID.
 * - Tự động khởi tạo Wishlist mới nếu người dùng chưa có.
 */
@Service
public class WishlistService {
    
    private static final Logger log = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setUserId(userId);
                    return w;
                });
        
        return mapToResponse(wishlist);
    }

    @Transactional
    public void addItem(String userId, UUID productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setUserId(userId);
                    return wishlistRepository.save(w);
                });
        
        wishlist.addItem(productId);
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeItem(String userId, UUID productId) {
        log.info("Removing product {} from wishlist for user {}", productId, userId);
        wishlistRepository.findByUserId(userId).ifPresent(wishlist -> {
            wishlist.removeItem(productId);
            wishlistRepository.save(wishlist);
        });
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setUserId(wishlist.getUserId());
        response.setName(wishlist.getName());
        response.setItems(wishlist.getItems().stream()
                .map(item -> new WishlistItemDto(item.getProductId(), item.getAddedAt()))
                .collect(Collectors.toList()));
        return response;
    }
}

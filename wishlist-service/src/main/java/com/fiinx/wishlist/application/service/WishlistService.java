package com.fiinx.wishlist.application.service;

import com.fiinx.wishlist.application.dto.WishlistResponse;
import com.fiinx.wishlist.domain.entity.Wishlist;
import com.fiinx.wishlist.domain.repository.WishlistRepository;
import com.fiinx.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> Wishlist.builder().userId(userId).build());
        
        return mapToResponse(wishlist);
    }

    @Transactional
    public void addItem(String userId, UUID productId) {
        log.info("Adding product {} to wishlist for user {}", productId, userId);
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().userId(userId).build()));
        
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
        // Correcting mapToResponse implementation (internal use of WishlistItemDto)
        return WishlistResponse.builder()
                .userId(wishlist.getUserId())
                .name(wishlist.getName())
                .items(wishlist.getItems().stream()
                        .map(item -> new com.fiinx.wishlist.application.dto.WishlistItemDto(item.getProductId(), item.getAddedAt()))
                        .collect(Collectors.toList()))
                .build();
    }
}

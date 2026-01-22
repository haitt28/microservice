package com.fiinx.wishlist.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.wishlist.application.dto.WishlistResponse;
import com.fiinx.wishlist.application.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "User wishlist management API")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist", description = "Retrieve the current user's wishlist products")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(userId)));
    }

    @PostMapping("/items/{productId}")
    @Operation(summary = "Add to wishlist", description = "Add a product to the user's wishlist")
    public ResponseEntity<ApiResponse<Void>> addItem(
            @PathVariable UUID productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        wishlistService.addItem(jwt.getSubject(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product added to wishlist"));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove from wishlist", description = "Remove a product from the user's wishlist")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @PathVariable UUID productId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        wishlistService.removeItem(jwt.getSubject(), productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product removed from wishlist"));
    }
}

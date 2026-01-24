package com.fiinx.wishlist.application.dto;

import java.time.Instant;
import java.util.UUID;

public class WishlistItemDto {
    private UUID productId;
    private Instant addedAt;

    public WishlistItemDto() {}
    public WishlistItemDto(UUID productId, Instant addedAt) {
        this.productId = productId;
        this.addedAt = addedAt;
    }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public Instant getAddedAt() { return addedAt; }
    public void setAddedAt(Instant addedAt) { this.addedAt = addedAt; }
}

package com.fiinx.wishlist.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private String userId;
    private String name;
    private List<WishlistItemDto> items;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WishlistItemDto {
    private UUID productId;
    private Instant addedAt;
}

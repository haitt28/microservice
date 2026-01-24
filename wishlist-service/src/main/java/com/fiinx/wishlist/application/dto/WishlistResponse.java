package com.fiinx.wishlist.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class WishlistResponse {
    private String userId;
    private String name;
    private List<WishlistItemDto> items;

    public WishlistResponse() {}
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<WishlistItemDto> getItems() { return items; }
    public void setItems(List<WishlistItemDto> items) { this.items = items; }
}

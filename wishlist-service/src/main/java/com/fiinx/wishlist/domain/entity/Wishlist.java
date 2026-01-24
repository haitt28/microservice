package com.fiinx.wishlist.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wishlists", indexes = {
    @Index(name = "idx_wishlist_user", columnList = "userId")
})
public class Wishlist extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String userId;

    private String name;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();

    public Wishlist() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<WishlistItem> getItems() { return items; }
    public void setItems(List<WishlistItem> items) { this.items = items; }

    public void addItem(UUID productId) {
        if (items.stream().noneMatch(item -> item.getProductId().equals(productId))) {
            WishlistItem item = new WishlistItem();
            item.setWishlist(this);
            item.setProductId(productId);
            items.add(item);
        }
    }

    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }
}

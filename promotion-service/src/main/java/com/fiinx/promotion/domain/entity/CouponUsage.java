package com.fiinx.promotion.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coupon_usage", indexes = {
    @Index(name = "idx_usage_coupon", columnList = "coupon_id"),
    @Index(name = "idx_usage_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(nullable = false)
    private String userId;
    
    private UUID orderId;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(nullable = false)
    private Instant usedAt;
}

package com.fiinx.promotion.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {
    
    @Column(unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(nullable = false, length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal minOrderValue;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    private Integer maxUsage;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer usageCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    @Column(nullable = false)
    private LocalDateTime validTo;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean oneTimePerUser = false;
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active && 
               now.isAfter(validFrom) && 
               now.isBefore(validTo) &&
               (maxUsage == null || usageCount < maxUsage);
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderValue) {
        if (!isValid()) return BigDecimal.ZERO;
        if (minOrderValue != null && orderValue.compareTo(minOrderValue) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = switch (type) {
            case PERCENTAGE -> orderValue.multiply(value).divide(BigDecimal.valueOf(100));
            case FIXED_AMOUNT -> value;
            default -> BigDecimal.ZERO;
        };
        
        if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
            discount = maxDiscountAmount;
        }
        
        return discount;
    }
}

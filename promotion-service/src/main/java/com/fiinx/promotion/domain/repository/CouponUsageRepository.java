package com.fiinx.promotion.domain.repository;

import com.fiinx.promotion.domain.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {
    boolean existsByCouponIdAndUserId(UUID couponId, String userId);
    long countByCouponId(UUID couponId);
}

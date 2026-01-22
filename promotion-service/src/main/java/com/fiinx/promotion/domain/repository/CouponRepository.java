package com.fiinx.promotion.domain.repository;

import com.fiinx.promotion.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByActiveTrue AndValidFromBeforeAndValidToAfter(LocalDateTime now1, LocalDateTime now2);
    boolean existsByCode(String code);
}

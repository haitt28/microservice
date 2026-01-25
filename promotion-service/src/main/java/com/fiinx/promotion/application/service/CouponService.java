package com.fiinx.promotion.application.service;

import com.fiinx.common.exception.BusinessException;
import com.fiinx.common.exception.ResourceNotFoundException;
import com.fiinx.promotion.application.dto.ValidationResult;
import com.fiinx.promotion.domain.entity.Coupon;
import com.fiinx.promotion.domain.entity.CouponUsage;
import com.fiinx.promotion.domain.repository.CouponRepository;
import com.fiinx.promotion.domain.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Senior Note: Coupon Service - Quản lý mã giảm giá.
 * 
 * - Kiểm tra tính hợp lệ của Coupon (thời gian, giá trị đơn hàng tối thiểu).
 * - Kiểm tra giới hạn sử dụng (ví dụ: mỗi user chỉ dùng 1 lần).
 * - Tính toán số tiền được giảm (Discount calculation).
 * - Ghi nhận lịch sử sử dụng Coupon.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    
    @Transactional(readOnly = true)
    public ValidationResult validateCoupon(String code, BigDecimal orderValue, String userId) {
        log.info("Validating coupon: code={}, orderValue={}, userId={}", code, orderValue, userId);
        
        Coupon coupon = couponRepository.findByCode(code)
            .orElse(null);
        
        if (coupon == null) {
            return ValidationResult.builder()
                .valid(false)
                .message("Không tìm thấy mã giảm giá")
                .build();
        }
        
        if (!coupon.isValid()) {
            return ValidationResult.builder()
                .valid(false)
                .message("Mã giảm giá không hợp lệ hoặc đã hết hạn")
                .build();
        }
        
        if (coupon.getMinOrderValue() != null && 
            orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            return ValidationResult.builder()
                .valid(false)
                .message(String.format("Giá trị đơn hàng tối thiểu phải là %s", coupon.getMinOrderValue()))
                .build();
        }
        
        if (coupon.getOneTimePerUser() && 
            couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), userId)) {
            return ValidationResult.builder()
                .valid(false)
                .message("Bạn đã sử dụng mã giảm giá này rồi")
                .build();
        }
        
        BigDecimal discount = coupon.calculateDiscount(orderValue);
        
        return ValidationResult.builder()
            .valid(true)
            .message("Mã giảm giá hợp lệ")
            .discountAmount(discount)
            .couponCode(code)
            .build();
    }
    
    @Transactional
    public void recordUsage(String code, String userId, UUID orderId, BigDecimal discountAmount) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
        
        CouponUsage usage = CouponUsage.builder()
            .coupon(coupon)
            .userId(userId)
            .orderId(orderId)
            .discountAmount(discountAmount)
            .usedAt(Instant.now())
            .build();
        
        couponUsageRepository.save(usage);
        
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
        
        log.info("Coupon usage recorded: code={}, userId={}", code, userId);
    }
}

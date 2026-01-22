package com.fiinx.promotion.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.promotion.application.dto.ValidationResult;
import com.fiinx.promotion.application.service.CouponService;
import com.fiinx.promotion.domain.entity.Coupon;
import com.fiinx.promotion.domain.repository.CouponRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Promotion and coupon API")
public class PromotionController {
    
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    
    @PostMapping("/promotions/validate")
    @Operation(summary = "Validate coupon", description = "Validate coupon code for order")
    public ResponseEntity<ApiResponse<ValidationResult>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderValue,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt != null ? jwt.getSubject() : "guest";
        ValidationResult result = couponService.validateCoupon(code, orderValue, userId);
        
        if (result.isValid()) {
            return ResponseEntity.ok(ApiResponse.success(result, "Coupon is valid"));
        } else {
            return ResponseEntity.ok(ApiResponse.error("INVALID_COUPON", result.getMessage()));
        }
    }
    
    @GetMapping("/promotions/active")
    @Operation(summary = "Get active promotions", description = "Get all currently active promotions")
    public ResponseEntity<ApiResponse<List<Coupon>>> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findByActiveTrueAndValidFromBeforeAndValidToAfter(now, now);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }
    
    @PostMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create coupon", description = "Create new coupon (Admin)")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@RequestBody Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("DUPLICATE_CODE", "Coupon code already exists"));
        }
        
        Coupon saved = couponRepository.save(coupon);
        return ResponseEntity.ok(ApiResponse.success(saved, "Coupon created"));
    }
    
    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List coupons", description = "List all coupons (Admin)")
    public ResponseEntity<ApiResponse<List<Coupon>>> listCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }
    
    @DeleteMapping("/admin/coupons/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete coupon", description = "Delete coupon (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable UUID id) {
        couponRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Coupon deleted"));
    }
}

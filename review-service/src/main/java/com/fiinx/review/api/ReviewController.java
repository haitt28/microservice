package com.fiinx.review.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.common.dto.PageResponse;
import com.fiinx.review.application.dto.CreateReviewRequest;
import com.fiinx.review.application.dto.ReviewResponse;
import com.fiinx.review.application.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Đánh giá", description = "API quản lý đánh giá sản phẩm")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "Lấy danh sách đánh giá", description = "Lấy danh sách các đánh giá đã được duyệt cho một sản phẩm cụ thể")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, pageable)));
    }

    @PostMapping("/reviews")
    @Operation(summary = "Tạo đánh giá", description = "Tạo một đánh giá sản phẩm mới (Yêu cầu xác thực)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        ReviewResponse response = reviewService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đánh giá đã được gửi và đang chờ kiểm duyệt"));
    }

    @PutMapping("/admin/reviews/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Duyệt đánh giá", description = "Phê duyệt một đánh giá đang chờ (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<Void>> approveReview(@PathVariable UUID id) {
        reviewService.approveReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đánh giá đã được duyệt"));
    }

    @PutMapping("/admin/reviews/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Từ chối đánh giá", description = "Từ chối một đánh giá đang chờ (Chỉ dành cho Admin)")
    public ResponseEntity<ApiResponse<Void>> rejectReview(@PathVariable UUID id) {
        reviewService.rejectReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đánh giá đã bị từ chối"));
    }
}

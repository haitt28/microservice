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
@Tag(name = "Reviews", description = "Product review API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "Get product reviews", description = "Get approved reviews for a specific product")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, pageable)));
    }

    @PostMapping("/reviews")
    @Operation(summary = "Create review", description = "Create a product review (requires authentication)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        ReviewResponse response = reviewService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Review submitted for moderation"));
    }

    @PutMapping("/admin/reviews/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve review", description = "Approve a pending review (Admin only)")
    public ResponseEntity<ApiResponse<Void>> approveReview(@PathVariable UUID id) {
        reviewService.approveReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Review approved"));
    }

    @PutMapping("/admin/reviews/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject review", description = "Reject a pending review (Admin only)")
    public ResponseEntity<ApiResponse<Void>> rejectReview(@PathVariable UUID id) {
        reviewService.rejectReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Review rejected"));
    }
}

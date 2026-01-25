package com.fiinx.review.application.service;

import com.fiinx.common.dto.PageResponse;
import com.fiinx.common.exception.BusinessException;
import com.fiinx.common.exception.ResourceNotFoundException;
import com.fiinx.review.application.dto.CreateReviewRequest;
import com.fiinx.review.application.dto.ReviewResponse;
import com.fiinx.review.application.mapper.ReviewMapper;
import com.fiinx.review.domain.entity.Review;
import com.fiinx.review.domain.entity.ReviewStatus;
import com.fiinx.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Senior Note: Review Service - Quản lý đánh giá sản phẩm.
 * 
 * - Xử lý logic tạo đánh giá mới (đảm bảo mỗi user chỉ đánh giá 1 lần/sản phẩm).
 * - Cơ chế duyệt đánh giá (Moderation) trước khi hiển thị.
 * - Tự động đồng bộ điểm đánh giá trung bình sang Product Service qua Event.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String userId) {
        log.info("Creating review for product: {} by user: {}", request.getProductId(), userId);

        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            throw new BusinessException("ALREADY_REVIEWED", "Bạn đã đánh giá sản phẩm này rồi", HttpStatus.BAD_REQUEST);
        }

        Review review = reviewMapper.toEntity(request);
        review.setUserId(userId);
        
        if (request.getImageUrls() != null) {
            request.getImageUrls().forEach(review::addImage);
        }

        review = reviewRepository.save(review);
        log.info("Review created with ID: {}", review.getId());

        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable);
        
        return PageResponse.from(page.map(reviewMapper::toResponse));
    }

    @Transactional
    public void approveReview(UUID reviewId) {
        log.info("Approving review: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setStatus(ReviewStatus.APPROVED);
        reviewRepository.save(review);

        // Thông báo cho Product Service để cập nhật lại điểm đánh giá (Rating)
        updateProductRating(review.getProductId());
    }

    @Transactional
    public void rejectReview(UUID reviewId) {
        log.info("Rejecting review: {}", reviewId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setStatus(ReviewStatus.REJECTED);
        reviewRepository.save(review);
    }

    private void updateProductRating(UUID productId) {
        Double avgRating = reviewRepository.getAverageRating(productId);
        Long count = reviewRepository.countApprovedReviews(productId);
        
        // Phát sự kiện (Event) sang Product Service
        // Đây là phiên bản đơn giản hóa của payload sự kiện
        log.info("Đang phát bản tin cập nhật rating cho sản phẩm: {}, avg: {}, count: {}", productId, avgRating, count);
        kafkaTemplate.send("product.rating.updated", productId.toString(), 
                new java.util.HashMap<String, Object>() {{
                    put("productId", productId);
                    put("averageRating", avgRating);
                    put("reviewCount", count);
                }});
    }
}

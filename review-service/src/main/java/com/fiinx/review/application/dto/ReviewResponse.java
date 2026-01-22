package com.fiinx.review.application.dto;

import com.fiinx.review.domain.entity.ReviewStatus;
import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private String userId;
    private Integer rating;
    private String title;
    private String comment;
    private ReviewStatus status;
    private Integer helpfulCount;
    private List<String> images;
    private Instant createdAt;
}

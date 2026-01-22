package com.fiinx.review.application.mapper;

import com.fiinx.review.application.dto.CreateReviewRequest;
import com.fiinx.review.application.dto.ReviewResponse;
import com.fiinx.review.domain.entity.Review;
import com.fiinx.review.domain.entity.ReviewImage;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    @Mapping(target = "images", ignore = true)
    Review toEntity(CreateReviewRequest request);

    @Mapping(target = "images", expression = "java(mapImages(review.getImages()))")
    ReviewResponse toResponse(Review review);

    default List<String> mapImages(List<ReviewImage> images) {
        if (images == null) return null;
        return images.stream().map(ReviewImage::getUrl).collect(Collectors.toList());
    }
}

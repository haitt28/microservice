package com.fiinx.review.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    @NotNull
    private UUID productId;
    
    @Min(1) @Max(5)
    private Integer rating;
    
    private String title;
    
    @NotBlank
    private String comment;
    
    private List<String> imageUrls;
    
    private UUID orderId; // Optional but recommended
}

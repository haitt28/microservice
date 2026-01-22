package com.fiinx.order.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineResponse {
    private UUID id;
    private UUID orderId;
    private String status;
    private String note;
    private Instant createdAt;
}

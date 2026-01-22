package com.fiinx.inventory.application.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckResponse {
    private UUID productId;
    private Integer availableQuantity;
    private Boolean isAvailable;
}

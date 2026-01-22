package com.fiinx.shipping.application.dto;

import com.fiinx.shipping.domain.entity.ShippingProvider;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private UUID id;
    private UUID orderId;
    private ShippingProvider provider;
    private String trackingCode;
    private BigDecimal shippingFee;
    private String status;
    private Instant estimatedDelivery;
}

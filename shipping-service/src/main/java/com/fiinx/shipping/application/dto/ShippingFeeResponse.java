package com.fiinx.shipping.application.dto;

import com.fiinx.shipping.domain.entity.ShippingProvider;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeResponse {
    private ShippingProvider provider;
    private BigDecimal fee;
    private Instant estimatedDelivery;
}

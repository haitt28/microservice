package com.fiinx.shipping.application.dto;

import com.fiinx.shipping.domain.entity.ShippingProvider;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {
    private UUID orderId;
    private ShippingProvider provider;
    private String senderAddress;
    private String receiverAddress;
    private java.math.BigDecimal weight;
}

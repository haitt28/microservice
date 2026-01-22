package com.fiinx.shipping.application.dto;

import com.fiinx.shipping.domain.entity.ShippingProvider;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeRequest {
    @NotNull
    private String fromAddress;
    
    @NotNull
    private String toAddress;
    
    @Min(0)
    private BigDecimal weight;
    
    private ShippingProvider provider;
}

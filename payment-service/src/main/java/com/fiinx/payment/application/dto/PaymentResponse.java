package com.fiinx.payment.application.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID paymentId;
    private String orderId;
    private String paymentUrl; // Redirect URL to mock gateway
    private String status;
}

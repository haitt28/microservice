package com.fiinx.payment.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.payment.application.dto.PaymentRequest;
import com.fiinx.payment.application.dto.PaymentResponse;
import com.fiinx.payment.application.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment gateway integration API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Get redirect URL for VNPay, MoMo, or ZaloPay")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.initiatePayment(request)));
    }

    @GetMapping("/callback/vnpay")
    @Operation(summary = "VNPay Callback", description = "IPN URL for VNPay mock")
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> params) {
        log.info("VNPay callback received: {}", params);
        UUID paymentId = UUID.fromString(params.get("vnp_TxnRef"));
        boolean success = "00".equals(params.get("vnp_ResponseCode"));
        paymentService.processWebhook(paymentId, success, params.get("vnp_TransactionNo"));
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/callback/momo")
    @Operation(summary = "MoMo Callback", description = "IPN URL for MoMo mock")
    public ResponseEntity<Void> momoCallback(@RequestBody Map<String, Object> body) {
        log.info("MoMo callback received: {}", body);
        UUID paymentId = UUID.fromString(body.get("orderId").toString());
        boolean success = (int)body.get("resultCode") == 0;
        paymentService.processWebhook(paymentId, success, body.get("transId").toString());
        return ResponseEntity.noContent().build();
    }
}

package com.fiinx.order.application.dto;

import com.fiinx.order.domain.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Order Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response")
public class OrderResponse {
    
    @Schema(description = "Order ID")
    private UUID id;
    
    @Schema(description = "Order number", example = "ORD-20240120-ABCD")
    private String orderNumber;
    
    @Schema(description = "Customer ID")
    private String customerId;
    
    @Schema(description = "Order status")
    private OrderStatus status;
    
    @Schema(description = "Order items")
    private List<OrderItemResponse> items;
    
    @Schema(description = "Subtotal before tax and shipping")
    private BigDecimal subtotal;
    
    @Schema(description = "Tax amount")
    private BigDecimal taxAmount;
    
    @Schema(description = "Shipping amount")
    private BigDecimal shippingAmount;
    
    @Schema(description = "Discount amount")
    private BigDecimal discountAmount;
    
    @Schema(description = "Total order amount")
    private BigDecimal totalAmount;
    
    @Schema(description = "Currency code")
    private String currency;
    
    @Schema(description = "Shipping address")
    private ShippingAddressResponse shippingAddress;
    
    @Schema(description = "Payment method")
    private String paymentMethod;
    
    @Schema(description = "Payment transaction ID")
    private String paymentId;
    
    @Schema(description = "Order notes")
    private String notes;
    
    @Schema(description = "Failure reason if order failed")
    private String failureReason;
    
    @Schema(description = "Order creation timestamp")
    private Instant createdAt;
    
    @Schema(description = "Order completion timestamp")
    private Instant completedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private String productId;
        private String productName;
        private String productSku;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountAmount;
        private BigDecimal subtotal;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressResponse {
        private String recipientName;
        private String phone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }
}

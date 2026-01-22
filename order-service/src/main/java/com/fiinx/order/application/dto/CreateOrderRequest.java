package com.fiinx.order.application.dto;

import com.fiinx.order.domain.entity.Order.ShippingAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * BEST PRACTICE #16: Request/Response DTOs
 * 
 * - Separate DTOs cho input và output
 * - Validation annotations
 * - OpenAPI documentation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new order")
public class CreateOrderRequest {
    
    @NotBlank(message = "Customer ID is required")
    @Size(max = 100, message = "Customer ID must not exceed 100 characters")
    @Schema(description = "Customer identifier", example = "cust-12345")
    private String customerId;
    
    @Email(message = "Invalid email format")
    @Schema(description = "Customer email for notifications", example = "customer@example.com")
    private String customerEmail;
    
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    @Schema(description = "List of order items")
    private List<OrderItemRequest> items;
    
    @Valid
    @Schema(description = "Shipping address")
    private ShippingAddressRequest shippingAddress;
    
    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String paymentMethod;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Schema(description = "Optional order notes")
    private String notes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @NotBlank(message = "Product ID is required")
        @Schema(description = "Product identifier", example = "prod-001")
        private String productId;
        
        @NotBlank(message = "Product name is required")
        @Schema(description = "Product name", example = "iPhone 15 Pro")
        private String productName;
        
        @Schema(description = "Product SKU", example = "IP15P-256-BLK")
        private String productSku;
        
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Quantity must not exceed 100")
        @Schema(description = "Quantity to order", example = "2")
        private int quantity;
        
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be positive")
        @Schema(description = "Price per unit", example = "29990000")
        private BigDecimal unitPrice;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddressRequest {
        
        @NotBlank(message = "Recipient name is required")
        @Schema(example = "Nguyen Van A")
        private String recipientName;
        
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number")
        @Schema(example = "0901234567")
        private String phone;
        
        @NotBlank(message = "Address is required")
        @Schema(example = "123 Le Loi Street")
        private String addressLine1;
        
        @Schema(example = "District 1")
        private String addressLine2;
        
        @NotBlank(message = "City is required")
        @Schema(example = "Ho Chi Minh City")
        private String city;
        
        @Schema(example = "")
        private String state;
        
        @Schema(example = "700000")
        private String postalCode;
        
        @Size(max = 2, message = "Country code must be 2 characters")
        @Schema(example = "VN")
        private String country;
        
        public ShippingAddress toEntity() {
            return ShippingAddress.builder()
                .recipientName(this.recipientName)
                .phone(this.phone)
                .addressLine1(this.addressLine1)
                .addressLine2(this.addressLine2)
                .city(this.city)
                .state(this.state)
                .postalCode(this.postalCode)
                .country(this.country)
                .build();
        }
    }
}

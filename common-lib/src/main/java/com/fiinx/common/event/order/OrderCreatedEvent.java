package com.fiinx.common.event.order;

import com.fiinx.common.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Event emitted when a new order is created
 * First step in the Order Saga
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends DomainEvent {
    
    private String orderId;
    private String customerId;
    private List<OrderItemPayload> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPayload {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
    
    @Override
    public void initializeDefaults(String source) {
        super.initializeDefaults("order-service");
    }
}

package com.fiinx.order.application.mapper;

import com.fiinx.order.application.dto.CreateOrderRequest;
import com.fiinx.order.application.dto.OrderResponse;
import com.fiinx.order.domain.entity.Order;
import com.fiinx.order.domain.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

/**
 * BEST PRACTICE #17: MapStruct Mapper
 * 
 * - Type-safe mapping with compile-time checking
 * - No reflection, better performance
 * - Automatic null checks
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    Order toEntity(CreateOrderRequest request);
    
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    OrderResponse toResponse(Order order);
    
    List<OrderResponse> toResponseList(List<Order> orders);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(CreateOrderRequest.OrderItemRequest itemRequest);
    
    List<OrderItem> toOrderItems(List<CreateOrderRequest.OrderItemRequest> itemRequests);
    
    OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem item);
    
    OrderResponse.ShippingAddressResponse toShippingAddressResponse(Order.ShippingAddress address);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order.ShippingAddress toShippingAddress(CreateOrderRequest.ShippingAddressRequest request);
}

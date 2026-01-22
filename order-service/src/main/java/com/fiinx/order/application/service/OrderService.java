package com.fiinx.order.application.service;

import com.fiinx.common.annotation.DistributedLock;
import com.fiinx.common.dto.PageResponse;
import com.fiinx.common.exception.ResourceNotFoundException;
import com.fiinx.order.application.dto.CreateOrderRequest;
import com.fiinx.order.application.dto.OrderResponse;
import com.fiinx.order.application.mapper.OrderMapper;
import com.fiinx.order.domain.entity.Order;
import com.fiinx.order.domain.entity.OrderItem;
import com.fiinx.order.domain.entity.OrderStatus;
import com.fiinx.order.domain.repository.OrderRepository;
import com.fiinx.order.infrastructure.saga.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Senior Note: Service Layer xử lý nghiệp vụ đơn hàng (Order Service).
 * 
 * - Đảm bảo tính tách biệt (Separation of concerns) giữa Controller và Logic xử lý.
 * - Quản lý giới hạn giao dịch (Transaction boundaries) để đảm bảo tính Acid.
 * - Điều phối các bước nghiệp vụ phức tạp liên quan đến Saga.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderSagaOrchestrator sagaOrchestrator;
    
    /**
     * Tạo đơn hàng mới với cơ chế Distributed Lock để chống việc gửi trùng lặp.
     * @DistributedLock ngăn chặn Double-click bằng cách sử dụng customer ID + sản phẩm đầu tiên làm Lock Key.
     */
    @DistributedLock(
        key = "'order:create:' + #request.customerId + ':' + #request.items[0].productId",
        waitTime = 0,  // Fail fast if lock not available
        leaseTime = 30
    )
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        // Build order
        Order order = buildOrder(request);
        
        // Save order
        order = orderRepository.save(order);
        log.info("Order created: {}", order.getOrderNumber());
        
        // Start saga asynchronously
        sagaOrchestrator.startOrderSaga(order);
        
        return orderMapper.toResponse(order);
    }
    
    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        return orderMapper.toResponse(order);
    }
    
    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        
        return orderMapper.toResponse(order);
    }
    
    /**
     * Get orders for customer with pagination
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getCustomerOrders(String customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerIdAndDeletedFalse(customerId, pageable);
        
        Page<OrderResponse> responsePage = orders.map(orderMapper::toResponse);
        return PageResponse.from(responsePage);
    }
    
    /**
     * Hủy đơn hàng (Nếu trạng thái hiện tại cho phép)
     */
    @DistributedLock(key = "'order:cancel:' + #orderNumber", leaseTime = 30)
    @Transactional
    public OrderResponse cancelOrder(String orderNumber, String reason) {
        Order order = orderRepository.findByOrderNumberForUpdate(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        
        if (order.getStatus().isTerminal()) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        
        // Trigger compensation saga if order was in progress
        if (order.getStatus() != OrderStatus.CREATED) {
            sagaOrchestrator.compensateOrder(order, reason);
        }
        
        order.markAsCancelled(reason);
        orderRepository.save(order);
        
        log.info("Order cancelled: {}", orderNumber);
        return orderMapper.toResponse(order);
    }
    
    // ==================== Private Helpers ====================
    
    private Order buildOrder(CreateOrderRequest request) {
        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .customerId(request.getCustomerId())
            .customerEmail(request.getCustomerEmail())
            .status(OrderStatus.CREATED)
            .paymentMethod(request.getPaymentMethod())
            .notes(request.getNotes())
            .shippingAddress(request.getShippingAddress() != null 
                ? request.getShippingAddress().toEntity() 
                : null)
            .taxAmount(BigDecimal.ZERO)
            .shippingAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .subtotal(BigDecimal.ZERO)
            .totalAmount(BigDecimal.ZERO)
            .build();
        
        // Add items
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = orderMapper.toOrderItem(itemRequest);
            item.calculateSubtotal();
            order.addItem(item);
        }
        
        order.recalculateTotals();
        return order;
    }
    
    private String generateOrderNumber() {
        // Format: ORD-YYYYMMDD-XXXX (random 4 chars)
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = String.format("%04X", ThreadLocalRandom.current().nextInt(0xFFFF));
        return String.format("ORD-%s-%s", date, random);
    }
}

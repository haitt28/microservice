package com.fiinx.order.domain.repository;

import com.fiinx.order.domain.entity.Order;
import com.fiinx.order.domain.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * BEST PRACTICE #15: Repository với Query Methods
 * 
 * - Sử dụng method naming convention
 * - Custom queries khi cần optimization
 * - Pessimistic locking cho critical operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    /**
     * Find by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find by order number with pessimistic lock
     * Dùng khi cần update order và muốn prevent concurrent modifications
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberForUpdate(@Param("orderNumber") String orderNumber);
    
    /**
     * Find orders by customer
     */
    Page<Order> findByCustomerIdAndDeletedFalse(String customerId, Pageable pageable);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatusAndDeletedFalse(OrderStatus status);
    
    /**
     * Find orders by customer and status
     */
    Page<Order> findByCustomerIdAndStatusAndDeletedFalse(
        String customerId, 
        OrderStatus status, 
        Pageable pageable
    );
    
    /**
     * Find stale pending orders (for cleanup/timeout)
     */
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses " +
           "AND o.createdAt < :cutoffTime AND o.deleted = false")
    List<Order> findStalePendingOrders(
        @Param("statuses") List<OrderStatus> statuses,
        @Param("cutoffTime") Instant cutoffTime
    );
    
    /**
     * Count orders by status for monitoring
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o WHERE o.deleted = false GROUP BY o.status")
    List<Object[]> countByStatus();
    
    /**
     * Check if order exists for customer
     */
    boolean existsByOrderNumberAndCustomerId(String orderNumber, String customerId);
    
    /**
     * Fetch order with items (avoid N+1)
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);
    
    /**
     * Fetch order with items by order number
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);
}

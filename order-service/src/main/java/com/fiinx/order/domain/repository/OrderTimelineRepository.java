package com.fiinx.order.domain.repository;

import com.fiinx.order.domain.entity.OrderTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderTimelineRepository extends JpaRepository<OrderTimeline, UUID> {
    List<OrderTimeline> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}

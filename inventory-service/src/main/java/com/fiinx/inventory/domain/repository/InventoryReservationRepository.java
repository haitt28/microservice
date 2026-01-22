package com.fiinx.inventory.domain.repository;

import com.fiinx.inventory.domain.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
    
    List<InventoryReservation> findByReservationId(String reservationId);
    
    List<InventoryReservation> findByOrderId(String orderId);
}

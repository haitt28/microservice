package com.fiinx.inventory.application.dto;

import com.fiinx.common.event.inventory.InventoryReserveCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Kết quả của việc giữ hàng trong kho.
 */
@Getter
@Builder
public class InventoryReservationResult {
    private boolean success;
    private String reservationId;
    private List<InventoryReserveCommand.ReservationItem> reservedItems;
    private List<InventoryReserveCommand.ReservationItem> failedItems;
    private String failureReason;

    public static InventoryReservationResult success(String reservationId, List<InventoryReserveCommand.ReservationItem> items) {
        return InventoryReservationResult.builder()
                .success(true)
                .reservationId(reservationId)
                .reservedItems(items)
                .build();
    }

    public static InventoryReservationResult failure(String reason, List<InventoryReserveCommand.ReservationItem> failedItems) {
        return InventoryReservationResult.builder()
                .success(false)
                .failureReason(reason)
                .failedItems(failedItems)
                .build();
    }
}

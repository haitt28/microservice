package com.fiinx.notification.api.controller;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.notification.application.service.NotificationService;
import com.fiinx.notification.domain.entity.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notification history and management API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get my notifications", description = "List all notifications for the current user")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getMyNotifications(principal.getName())));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    @PostMapping("/admin/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Broadcast notification", description = "Send a notification to all active users via WebSocket")
    public ResponseEntity<ApiResponse<Void>> broadcast(@RequestParam String title, @RequestParam String message) {
        notificationService.broadcast(title, message);
        return ResponseEntity.ok(ApiResponse.success(null, "Broadcast sent"));
    }
}

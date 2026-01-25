package com.fiinx.notification.application.service;

import com.fiinx.notification.domain.entity.Notification;
import com.fiinx.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Senior Note: Notification Service - Dịch vụ thông báo.
 * 
 * - Gửi thông báo đến người dùng qua WebSocket (Real-time).
 * - Lưu trữ lịch sử thông báo vào Database.
 * - Hỗ trợ đánh dấu đã đọc (Mark as read).
 * - Hỗ trợ phát bản tin chung (Broadcast) cho toàn bộ hệ thống.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendNotification(String userId, String title, String message, String type) {
        log.info("Sending notification to user {}: {}", userId, title);
        
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .build();
        
        notification = notificationRepository.save(notification);

        // Chuyển phát thời gian thực (Real-time delivery) qua WebSocket
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getMyNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void broadcast(String title, String message) {
        log.info("Đang phát bản tin thông báo: {}", title);
        // Trong kịch bản thực tế, phương thức này sẽ lặp qua danh sách user hoặc đẩy vào một global topic
        messagingTemplate.convertAndSend("/topic/broadcast", title + ": " + message);
    }
}

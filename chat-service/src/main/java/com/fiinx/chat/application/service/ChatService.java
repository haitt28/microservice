package com.fiinx.chat.application.service;

import com.fiinx.chat.application.dto.ChatMessageDto;
import com.fiinx.chat.domain.entity.ChatMessage;
import com.fiinx.chat.domain.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Senior Note: Chat Service - Dịch vụ trò chuyện thời gian thực.
 * 
 * - Lưu trữ tin nhắn vào Database để tra cứu lịch sử.
 * - Chuyển tiếp tin nhắn đến các Topic WebSocket tương ứng.
 * - Xử lý theo từng Conversation ID để phân tách phòng chat.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processMessage(ChatMessageDto messageDto) {
        log.info("Processing message from {} in conversation {}", messageDto.getSenderId(), messageDto.getConversationId());
        
        ChatMessage message = ChatMessage.builder()
                .conversationId(messageDto.getConversationId())
                .senderId(messageDto.getSenderId())
                .content(messageDto.getContent())
                .sentAt(Instant.now())
                .build();
                
        chatMessageRepository.save(message);

        // Chuyển tiếp tin nhắn đến Topic tương ứng (WebSocket)
        messagingTemplate.convertAndSend("/topic/messages." + message.getConversationId(), messageDto);
    }
}

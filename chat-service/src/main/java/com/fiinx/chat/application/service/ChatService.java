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

        // Forward message to topic
        messagingTemplate.convertAndSend("/topic/messages." + message.getConversationId(), messageDto);
    }
}

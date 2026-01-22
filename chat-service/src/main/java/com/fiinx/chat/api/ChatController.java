package com.fiinx.chat.api;

import com.fiinx.chat.application.dto.ChatMessageDto;
import com.fiinx.chat.application.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto messageDto) {
        log.info("Received message via WebSocket: {}", messageDto);
        chatService.processMessage(messageDto);
    }
}

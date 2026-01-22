package com.fiinx.chat.application.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String senderId;
    private String content;
    private UUID conversationId;
    private Instant sentAt;
}

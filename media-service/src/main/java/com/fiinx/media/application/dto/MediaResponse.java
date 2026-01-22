package com.fiinx.media.application.dto;

import com.fiinx.media.domain.entity.MediaType;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private UUID id;
    private String fileName;
    private String originalFileName;
    private MediaType type;
    private Long fileSize;
    private String mimeType;
    private String cdnUrl;
    private Instant createdAt;
}

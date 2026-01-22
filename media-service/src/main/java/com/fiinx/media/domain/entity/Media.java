package com.fiinx.media.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    private Long fileSize;

    private String mimeType;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String cdnUrl;

    private String uploadedBy; // User ID
}

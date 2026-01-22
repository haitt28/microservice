package com.fiinx.media.api;

import com.fiinx.common.dto.ApiResponse;
import com.fiinx.media.application.dto.MediaResponse;
import com.fiinx.media.application.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Media upload and retrieval API")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    @Operation(summary = "Upload media file", description = "Upload images, videos, or documents")
    public ResponseEntity<ApiResponse<MediaResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        MediaResponse response = mediaService.uploadFile(file, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "File uploaded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get media info", description = "Get details and CDN URL for a file")
    public ResponseEntity<ApiResponse<MediaResponse>> getMediaInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(mediaService.getMediaInfo(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete media", description = "Remove a file from storage and database")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        mediaService.deleteMedia(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Media deleted successfully"));
    }
}

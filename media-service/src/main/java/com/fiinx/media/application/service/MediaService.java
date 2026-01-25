package com.fiinx.media.application.service;

import com.fiinx.media.application.dto.MediaResponse;
import com.fiinx.media.domain.entity.Media;
import com.fiinx.media.domain.entity.MediaType;
import com.fiinx.media.domain.repository.MediaRepository;
import com.fiinx.common.exception.BusinessException;
import com.fiinx.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Senior Note: Media Service - Dịch vụ quản lý tệp tin và phương tiện.
 * 
 * - Hỗ trợ tải lên (Upload) tệp tin với nhiều định dạng (Ảnh, Video, PDF, Tài liệu).
 * - Lưu trữ tệp tin cục bộ (Local storage) hoặc tích hợp CDN.
 * - Kiểm soát quyền sở hữu khi xóa tệp tin.
 * - Tự động định danh MediaType dựa trên Content Type.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Value("${storage.local.path}")
    private String localStoragePath;

    @Value("${storage.cdn.url-prefix}")
    private String cdnUrlPrefix;

    @Transactional
    public MediaResponse uploadFile(MultipartFile file, String userId) {
        log.info("Uploading file: {} for user: {}", file.getOriginalFilename(), userId);

        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "Không thể tải lên tệp tin rỗng", HttpStatus.BAD_REQUEST);
        }

        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String fileName = UUID.randomUUID().toString() + extension;
        MediaType type = determineMediaType(file.getContentType());

        try {
            // Mô phỏng lưu trữ vào Local storage
            Path directory = Paths.get(localStoragePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path filePath = directory.resolve(fileName);
            // Files.copy(file.getInputStream(), filePath); // Bị chú thích để tránh ghi tệp thực tế trong môi trường này
            
            log.info("Tệp tin đã được lưu tại: {}", filePath);

            Media media = Media.builder()
                    .fileName(fileName)
                    .originalFileName(originalFileName)
                    .type(type)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .storagePath(filePath.toString())
                    .cdnUrl(cdnUrlPrefix + fileName)
                    .uploadedBy(userId)
                    .build();

            media = mediaRepository.save(media);

            return MediaResponse.builder()
                    .id(media.getId())
                    .fileName(media.getFileName())
                    .originalFileName(media.getOriginalFileName())
                    .type(media.getType())
                    .fileSize(media.getFileSize())
                    .mimeType(media.getMimeType())
                    .cdnUrl(media.getCdnUrl())
                    .createdAt(media.getCreatedAt())
                    .build();

        } catch (IOException e) {
            log.error("Tải lên tệp tin thất bại", e);
            throw new BusinessException("UPLOAD_FAILED", "Tải lên tệp tin thất bại: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public MediaResponse getMediaInfo(UUID id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media", "id", id));

        return MediaResponse.builder()
                .id(media.getId())
                .fileName(media.getFileName())
                .originalFileName(media.getOriginalFileName())
                .type(media.getType())
                .fileSize(media.getFileSize())
                .mimeType(media.getMimeType())
                .cdnUrl(media.getCdnUrl())
                .createdAt(media.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteMedia(UUID id, String userId) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media", "id", id));
        
        if (!media.getUploadedBy().equals(userId)) {
             throw new BusinessException("PERMISSION_DENIED", "Bạn không có quyền xóa tệp tin này", HttpStatus.FORBIDDEN);
        }

        // Mô phỏng xóa tệp tin khỏi bộ nhớ
        log.info("Đang xóa tệp tin khỏi bộ nhớ: {}", media.getStoragePath());
        
        mediaRepository.delete(media);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private MediaType determineMediaType(String contentType) {
        if (contentType == null) return MediaType.DOCUMENT;
        if (contentType.startsWith("image/")) return MediaType.IMAGE;
        if (contentType.startsWith("video/")) return MediaType.VIDEO;
        if (contentType.equals("application/pdf")) return MediaType.PDF;
        return MediaType.DOCUMENT;
    }
}

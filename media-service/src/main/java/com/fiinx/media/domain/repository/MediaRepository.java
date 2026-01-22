package com.fiinx.media.domain.repository;

import com.fiinx.media.domain.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByUploadedBy(String uploadedBy);
}

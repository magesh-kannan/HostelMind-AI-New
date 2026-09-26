package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.infrastructure.persistence.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataAnnouncementRepository extends JpaRepository<AnnouncementEntity, UUID> {
    List<AnnouncementEntity> findByCategory(AnnouncementCategory category);
    List<AnnouncementEntity> findByPinnedTrue();
}

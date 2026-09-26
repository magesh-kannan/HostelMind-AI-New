package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.AnnouncementReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAnnouncementReactionRepository extends JpaRepository<AnnouncementReactionEntity, UUID> {
    Optional<AnnouncementReactionEntity> findByAnnouncementIdAndUserId(UUID announcementId, UUID userId);
    void deleteByAnnouncementIdAndUserId(UUID announcementId, UUID userId);
    long countByAnnouncementId(UUID announcementId);
}

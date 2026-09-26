package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Announcement;
import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.domain.port.AnnouncementRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.AnnouncementEntity;
import com.hostelmind.infrastructure.persistence.entity.AnnouncementReactionEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataAnnouncementReactionRepository;
import com.hostelmind.infrastructure.persistence.repository.SpringDataAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnnouncementRepositoryAdapter implements AnnouncementRepositoryPort {

    private final SpringDataAnnouncementRepository announcementRepository;
    private final SpringDataAnnouncementReactionRepository reactionRepository;

    @Override
    public Announcement save(Announcement announcement) {
        AnnouncementEntity entity = mapToEntity(announcement);
        AnnouncementEntity saved = announcementRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Announcement> findById(UUID id) {
        return announcementRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findByCategory(AnnouncementCategory category) {
        return announcementRepository.findByCategory(category).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> findPinned() {
        return announcementRepository.findByPinnedTrue().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        announcementRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addOrUpdateReaction(UUID announcementId, UUID userId, String reaction) {
        Optional<AnnouncementReactionEntity> existing = reactionRepository.findByAnnouncementIdAndUserId(announcementId, userId);
        if (existing.isPresent()) {
            AnnouncementReactionEntity entity = existing.get();
            entity.setReaction(reaction);
            reactionRepository.save(entity);
        } else {
            AnnouncementReactionEntity entity = AnnouncementReactionEntity.builder()
                    .announcementId(announcementId)
                    .userId(userId)
                    .reaction(reaction)
                    .build();
            reactionRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void removeReaction(UUID announcementId, UUID userId) {
        reactionRepository.deleteByAnnouncementIdAndUserId(announcementId, userId);
    }

    @Override
    public long countReactions(UUID announcementId) {
        return reactionRepository.countByAnnouncementId(announcementId);
    }

    private AnnouncementEntity mapToEntity(Announcement domain) {
        return AnnouncementEntity.builder()
                .id(domain.getId())
                .authorId(domain.getAuthorId())
                .hostelId(domain.getHostelId())
                .title(domain.getTitle())
                .body(domain.getBody())
                .category(domain.getCategory())
                .priority(domain.getPriority())
                .pinned(domain.isPinned())
                .expiresAt(domain.getExpiresAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Announcement mapToDomain(AnnouncementEntity entity) {
        return Announcement.builder()
                .id(entity.getId())
                .authorId(entity.getAuthorId())
                .hostelId(entity.getHostelId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .category(entity.getCategory())
                .priority(entity.getPriority())
                .pinned(entity.isPinned())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

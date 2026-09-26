package com.hostelmind.application.service;

import com.hostelmind.application.dto.community.AnnouncementDto;
import com.hostelmind.application.dto.community.CreateAnnouncementRequest;
import com.hostelmind.domain.model.Announcement;
import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.domain.model.AnnouncementPriority;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AnnouncementRepositoryPort;
import com.hostelmind.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityUseCase {

    private final AnnouncementRepositoryPort announcementRepository;
    private final UserRepositoryPort userRepository;

    public AnnouncementDto createAnnouncement(UUID authorId, CreateAnnouncementRequest request) {
        Announcement announcement = Announcement.builder()
                .authorId(authorId)
                .hostelId(request.getHostelId())
                .title(request.getTitle())
                .body(request.getBody())
                .category(request.getCategory())
                .priority(request.getPriority() != null ? request.getPriority() : AnnouncementPriority.NORMAL)
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .expiresAt(request.getExpiresAt())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Announcement saved = announcementRepository.save(announcement);
        return mapToDto(saved);
    }

    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .sorted(Comparator.comparing(Announcement::isPinned).reversed()
                        .thenComparing(Announcement::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AnnouncementDto> getAnnouncementsByCategory(AnnouncementCategory category) {
        return announcementRepository.findByCategory(category).stream()
                .sorted(Comparator.comparing(Announcement::isPinned).reversed()
                        .thenComparing(Announcement::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AnnouncementDto getAnnouncementById(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        return mapToDto(announcement);
    }

    public void deleteAnnouncement(UUID id) {
        announcementRepository.deleteById(id);
    }

    public void reactToAnnouncement(UUID announcementId, UUID userId, String reaction) {
        announcementRepository.addOrUpdateReaction(announcementId, userId, reaction);
    }

    public void removeReaction(UUID announcementId, UUID userId) {
        announcementRepository.removeReaction(announcementId, userId);
    }

    private AnnouncementDto mapToDto(Announcement announcement) {
        String authorName = "System Admin";
        if (announcement.getAuthorId() != null) {
            authorName = userRepository.findById(announcement.getAuthorId())
                    .map(User::getFullName)
                    .orElse("Unknown Author");
        }

        long count = announcementRepository.countReactions(announcement.getId());

        return AnnouncementDto.builder()
                .id(announcement.getId())
                .authorId(announcement.getAuthorId())
                .authorName(authorName)
                .hostelId(announcement.getHostelId())
                .title(announcement.getTitle())
                .body(announcement.getBody())
                .category(announcement.getCategory())
                .priority(announcement.getPriority())
                .pinned(announcement.isPinned())
                .expiresAt(announcement.getExpiresAt())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .reactionCount(count)
                .build();
    }
}

package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Announcement;
import com.hostelmind.domain.model.AnnouncementCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepositoryPort {
    Announcement save(Announcement announcement);
    Optional<Announcement> findById(UUID id);
    List<Announcement> findAll();
    List<Announcement> findByCategory(AnnouncementCategory category);
    List<Announcement> findPinned();
    void deleteById(UUID id);
    
    // Reactions
    void addOrUpdateReaction(UUID announcementId, UUID userId, String reaction);
    void removeReaction(UUID announcementId, UUID userId);
    long countReactions(UUID announcementId);
}

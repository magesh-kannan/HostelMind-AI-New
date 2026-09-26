package com.hostelmind.application.dto.community;

import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.domain.model.AnnouncementPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {
    private UUID id;
    private UUID authorId;
    private String authorName;
    private UUID hostelId;
    private String title;
    private String body;
    private AnnouncementCategory category;
    private AnnouncementPriority priority;
    private boolean pinned;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
    private long reactionCount;
}

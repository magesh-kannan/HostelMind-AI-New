package com.hostelmind.domain.model;

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
public class Announcement {
    private UUID id;
    private UUID authorId;
    private UUID hostelId;
    private String title;
    private String body;
    private AnnouncementCategory category;
    private AnnouncementPriority priority;
    private boolean pinned;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
}

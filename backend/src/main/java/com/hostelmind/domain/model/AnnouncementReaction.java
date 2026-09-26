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
public class AnnouncementReaction {
    private UUID id;
    private UUID announcementId;
    private UUID userId;
    private String reaction;
    private Instant createdAt;
}

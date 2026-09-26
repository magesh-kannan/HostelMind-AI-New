package com.hostelmind.application.dto.community;

import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.domain.model.AnnouncementPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAnnouncementRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    @NotNull(message = "Category is required")
    private AnnouncementCategory category;

    private AnnouncementPriority priority;
    private Boolean pinned;
    private UUID hostelId;
    private Instant expiresAt;
}

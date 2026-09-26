package com.hostelmind.infrastructure.web.controller;

import com.hostelmind.application.dto.community.AnnouncementDto;
import com.hostelmind.application.dto.community.AnnouncementReactionRequest;
import com.hostelmind.application.dto.community.CreateAnnouncementRequest;
import com.hostelmind.application.service.CommunityUseCase;
import com.hostelmind.domain.model.AnnouncementCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityUseCase communityUseCase;

    @PostMapping("/announcements")
    public ResponseEntity<AnnouncementDto> createAnnouncement(
            Authentication authentication,
            @Valid @RequestBody CreateAnnouncementRequest request
    ) {
        UUID authorId = extractUserId(authentication);
        AnnouncementDto dto = communityUseCase.createAnnouncement(authorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncements(
            @RequestParam(required = false) AnnouncementCategory category
    ) {
        if (category != null) {
            return ResponseEntity.ok(communityUseCase.getAnnouncementsByCategory(category));
        }
        return ResponseEntity.ok(communityUseCase.getAllAnnouncements());
    }

    @GetMapping("/announcements/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncementById(@PathVariable UUID id) {
        return ResponseEntity.ok(communityUseCase.getAnnouncementById(id));
    }

    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable UUID id) {
        communityUseCase.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/announcements/{id}/reactions")
    public ResponseEntity<Void> reactToAnnouncement(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody AnnouncementReactionRequest request
    ) {
        UUID userId = extractUserId(authentication);
        communityUseCase.reactToAnnouncement(id, userId, request.getReaction());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/announcements/{id}/reactions")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        communityUseCase.removeReaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }
}

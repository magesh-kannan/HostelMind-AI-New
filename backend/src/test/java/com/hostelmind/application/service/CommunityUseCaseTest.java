package com.hostelmind.application.service;

import com.hostelmind.application.dto.community.AnnouncementDto;
import com.hostelmind.application.dto.community.CreateAnnouncementRequest;
import com.hostelmind.domain.model.Announcement;
import com.hostelmind.domain.model.AnnouncementCategory;
import com.hostelmind.domain.model.AnnouncementPriority;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AnnouncementRepositoryPort;
import com.hostelmind.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityUseCaseTest {

    @Mock
    private AnnouncementRepositoryPort announcementRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private CommunityUseCase communityUseCase;

    private UUID authorId;
    private UUID announcementId;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        announcementId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create announcement successfully")
    void createAnnouncement_Success() {
        CreateAnnouncementRequest req = CreateAnnouncementRequest.builder()
                .title("Inter-Hostel Sports Fest")
                .body("Registrations close this Friday.")
                .category(AnnouncementCategory.SPORTS)
                .priority(AnnouncementPriority.HIGH)
                .pinned(true)
                .build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(
                User.builder().id(authorId).fullName("Warden Sharma").build()
        ));

        when(announcementRepository.save(any())).thenAnswer(inv -> {
            Announcement a = inv.getArgument(0);
            a.setId(announcementId);
            return a;
        });

        when(announcementRepository.countReactions(announcementId)).thenReturn(5L);

        AnnouncementDto dto = communityUseCase.createAnnouncement(authorId, req);

        assertNotNull(dto);
        assertEquals(announcementId, dto.getId());
        assertEquals("Inter-Hostel Sports Fest", dto.getTitle());
        assertEquals("Warden Sharma", dto.getAuthorName());
        assertEquals(AnnouncementCategory.SPORTS, dto.getCategory());
        assertEquals(AnnouncementPriority.HIGH, dto.getPriority());
        assertTrue(dto.isPinned());
        assertEquals(5L, dto.getReactionCount());

        verify(announcementRepository).save(any());
    }

    @Test
    @DisplayName("Should retrieve all announcements sorted pinned first")
    void getAllAnnouncements_Sorted() {
        Announcement normal = Announcement.builder()
                .id(UUID.randomUUID())
                .title("Normal Post")
                .category(AnnouncementCategory.GENERAL)
                .pinned(false)
                .createdAt(Instant.now().minusSeconds(100))
                .build();

        Announcement pinned = Announcement.builder()
                .id(UUID.randomUUID())
                .title("Pinned Post")
                .category(AnnouncementCategory.EMERGENCY)
                .pinned(true)
                .createdAt(Instant.now().minusSeconds(500))
                .build();

        when(announcementRepository.findAll()).thenReturn(List.of(normal, pinned));

        List<AnnouncementDto> list = communityUseCase.getAllAnnouncements();

        assertEquals(2, list.size());
        assertEquals("Pinned Post", list.get(0).getTitle()); // Pinned should be first
        assertEquals("Normal Post", list.get(1).getTitle());
    }

    @Test
    @DisplayName("Should add and remove reactions")
    void handleReactions() {
        UUID userId = UUID.randomUUID();

        communityUseCase.reactToAnnouncement(announcementId, userId, "LIKE");
        verify(announcementRepository).addOrUpdateReaction(announcementId, userId, "LIKE");

        communityUseCase.removeReaction(announcementId, userId);
        verify(announcementRepository).removeReaction(announcementId, userId);
    }
}

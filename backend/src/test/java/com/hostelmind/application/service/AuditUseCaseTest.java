package com.hostelmind.application.service;

import com.hostelmind.application.dto.AuditLogDto;
import com.hostelmind.application.dto.AuditStatsDto;
import com.hostelmind.application.dto.UserDto;
import com.hostelmind.domain.model.AuditLog;
import com.hostelmind.domain.model.Role;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditUseCaseTest {

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private AuditUseCase auditUseCase;

    private UUID userId;
    private AuditLog log1, log2, log3;
    private User user1;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        log1 = AuditLog.builder()
                .id(UUID.randomUUID()).userId(userId)
                .action("CREATE_COMPLAINT").entityType("Complaint")
                .entityId(UUID.randomUUID().toString())
                .details("Complaint created").createdAt(Instant.now().minusSeconds(300))
                .build();

        log2 = AuditLog.builder()
                .id(UUID.randomUUID()).userId(userId)
                .action("TRIGGER_EMERGENCY_SOS").entityType("EmergencySosAlert")
                .entityId(UUID.randomUUID().toString())
                .details("SOS triggered").createdAt(Instant.now().minusSeconds(60))
                .build();

        log3 = AuditLog.builder()
                .id(UUID.randomUUID()).userId(UUID.randomUUID())
                .action("CREATE_INVOICE").entityType("Invoice")
                .entityId(UUID.randomUUID().toString())
                .details("Invoice created").createdAt(Instant.now())
                .build();

        user1 = User.builder()
                .id(userId).email("student@example.com")
                .fullName("Riya Sharma").phoneNumber("9876543210")
                .active(true).roles(Set.of(Role.ROLE_STUDENT))
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
    }

    // ── getAllLogs ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all audit logs sorted by createdAt descending")
    void getAllLogs_ReturnsSortedDescending() {
        when(auditLogRepository.findAll()).thenReturn(List.of(log1, log3, log2));

        List<AuditLogDto> result = auditUseCase.getAllLogs();

        assertEquals(3, result.size());
        // log3 is most recent, should be first
        assertEquals(log3.getId(), result.get(0).getId());
        assertEquals(log2.getId(), result.get(1).getId());
        assertEquals(log1.getId(), result.get(2).getId());
    }

    @Test
    @DisplayName("Should return empty list when no logs exist")
    void getAllLogs_EmptyList() {
        when(auditLogRepository.findAll()).thenReturn(List.of());
        assertTrue(auditUseCase.getAllLogs().isEmpty());
    }

    // ── getLogsByUser ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return logs filtered by userId")
    void getLogsByUser_Success() {
        when(auditLogRepository.findByUserId(userId)).thenReturn(List.of(log1, log2));

        List<AuditLogDto> result = auditUseCase.getLogsByUser(userId);

        assertEquals(2, result.size());
        result.forEach(dto -> assertEquals(userId, dto.getUserId()));
        verify(auditLogRepository).findByUserId(userId);
    }

    // ── getLogsByEntityType ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should return logs filtered by entity type")
    void getLogsByEntityType_Success() {
        when(auditLogRepository.findByEntityType("Complaint")).thenReturn(List.of(log1));

        List<AuditLogDto> result = auditUseCase.getLogsByEntityType("Complaint");

        assertEquals(1, result.size());
        assertEquals("Complaint", result.get(0).getEntityType());
        verify(auditLogRepository).findByEntityType("Complaint");
    }

    // ── getLogsByAction ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return logs filtered by action keyword")
    void getLogsByAction_Success() {
        when(auditLogRepository.findByAction("SOS")).thenReturn(List.of(log2));

        List<AuditLogDto> result = auditUseCase.getLogsByAction("SOS");

        assertEquals(1, result.size());
        assertEquals("TRIGGER_EMERGENCY_SOS", result.get(0).getAction());
        verify(auditLogRepository).findByAction("SOS");
    }

    // ── getAuditStats ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should compute accurate audit stats grouped by entity type and action")
    void getAuditStats_Success() {
        when(auditLogRepository.findAll()).thenReturn(List.of(log1, log2, log3));

        AuditStatsDto stats = auditUseCase.getAuditStats();

        assertNotNull(stats);
        assertEquals(3, stats.getTotalEvents());

        // byEntityType
        assertEquals(1L, stats.getByEntityType().get("Complaint"));
        assertEquals(1L, stats.getByEntityType().get("EmergencySosAlert"));
        assertEquals(1L, stats.getByEntityType().get("Invoice"));

        // byAction — each action appears once
        assertEquals(1L, stats.getByAction().get("CREATE_COMPLAINT"));
        assertEquals(1L, stats.getByAction().get("TRIGGER_EMERGENCY_SOS"));
        assertEquals(1L, stats.getByAction().get("CREATE_INVOICE"));
    }

    // ── getAllUsers ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all users mapped to UserDto")
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        List<UserDto> result = auditUseCase.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Riya Sharma", result.get(0).getFullName());
        assertEquals("student@example.com", result.get(0).getEmail());
        assertTrue(result.get(0).isActive());
        assertTrue(result.get(0).getRoles().contains(Role.ROLE_STUDENT));
    }

    // ── getUserById ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return user by ID")
    void getUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        UserDto dto = auditUseCase.getUserById(userId);

        assertEquals(userId, dto.getId());
        assertEquals("Riya Sharma", dto.getFullName());
    }

    @Test
    @DisplayName("Should throw when user not found")
    void getUserById_NotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> auditUseCase.getUserById(userId));
    }
}

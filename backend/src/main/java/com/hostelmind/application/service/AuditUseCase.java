package com.hostelmind.application.service;

import com.hostelmind.application.dto.*;
import com.hostelmind.domain.model.AuditLog;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditUseCase {

    private final AuditLogRepositoryPort auditLogRepository;
    private final UserRepositoryPort userRepository;

    // ─── Audit Logs ──────────────────────────────────────────────────────────

    public List<AuditLogDto> getAllLogs() {
        return auditLogRepository.findAll().stream()
                .sorted(Comparator.comparing(AuditLog::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AuditLogDto> getLogsByUser(UUID userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(AuditLog::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AuditLogDto> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType).stream()
                .sorted(Comparator.comparing(AuditLog::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AuditLogDto> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .sorted(Comparator.comparing(AuditLog::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AuditStatsDto getAuditStats() {
        List<AuditLog> all = auditLogRepository.findAll();

        Map<String, Long> byEntity = all.stream()
                .filter(l -> l.getEntityType() != null)
                .collect(Collectors.groupingBy(AuditLog::getEntityType, Collectors.counting()));

        Map<String, Long> byAction = all.stream()
                .filter(l -> l.getAction() != null)
                .collect(Collectors.groupingBy(AuditLog::getAction, Collectors.counting()));

        return AuditStatsDto.builder()
                .totalEvents(all.size())
                .byEntityType(byEntity)
                .byAction(byAction)
                .build();
    }

    // ─── User listing (for Digital ID card & admin) ──────────────────────────

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return mapUserToDto(user);
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    private AuditLogDto mapToDto(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .roles(user.getRoles())
                .build();
    }
}

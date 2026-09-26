package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.AuditLogDto;
import com.hostelmind.application.dto.AuditStatsDto;
import com.hostelmind.application.dto.UserDto;
import com.hostelmind.application.service.AuditUseCase;
import com.hostelmind.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditUseCase auditUseCase;

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @GetMapping("/logs")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<AuditLogDto>> getAllLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) UUID userId) {

        List<AuditLogDto> result;
        if (userId != null) {
            result = auditUseCase.getLogsByUser(userId);
        } else if (entityType != null) {
            result = auditUseCase.getLogsByEntityType(entityType);
        } else if (action != null) {
            result = auditUseCase.getLogsByAction(action);
        } else {
            result = auditUseCase.getAllLogs();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logs/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AuditLogDto>> getMyLogs(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(auditUseCase.getLogsByUser(principal.getId()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<AuditStatsDto> getStats() {
        return ResponseEntity.ok(auditUseCase.getAuditStats());
    }

    // ── User Directory (for Digital ID lookup) ────────────────────────────────

    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(auditUseCase.getAllUsers());
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditUseCase.getUserById(userId));
    }
}

package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.*;
import com.hostelmind.application.service.ComplaintUseCase;
import com.hostelmind.domain.model.ComplaintStatus;
import com.hostelmind.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintUseCase complaintUseCase;

    // ── Student: create complaint ─────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<ComplaintDto> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ComplaintDto dto = complaintUseCase.createComplaint(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // ── Get by ID ─────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDto> getComplaintById(@PathVariable UUID id) {
        return ResponseEntity.ok(complaintUseCase.getComplaintById(id));
    }

    // ── Status history (audit trail) ──────────────────────────────

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ComplaintStatusHistoryDto>> getStatusHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(complaintUseCase.getStatusHistory(id));
    }

    // ── State machine transition ──────────────────────────────────

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STAFF', 'ROLE_STUDENT')")
    public ResponseEntity<ComplaintDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateComplaintStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ComplaintDto dto = complaintUseCase.transitionStatus(id, principal.getId(), request);
        return ResponseEntity.ok(dto);
    }

    // ── Listing endpoints ─────────────────────────────────────────

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
    public ResponseEntity<List<ComplaintDto>> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(complaintUseCase.getComplaintsByStudent(principal.getId()));
    }

    @GetMapping("/hostel/{hostelId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<ComplaintDto>> getByHostel(@PathVariable UUID hostelId) {
        return ResponseEntity.ok(complaintUseCase.getComplaintsByHostel(hostelId));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STAFF')")
    public ResponseEntity<List<ComplaintDto>> getAssignedToMe(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(complaintUseCase.getComplaintsAssignedTo(principal.getId()));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<ComplaintDto>> getByStatus(@PathVariable ComplaintStatus status) {
        return ResponseEntity.ok(complaintUseCase.getComplaintsByStatus(status));
    }

    // ── Stats ─────────────────────────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<ComplaintStatsDto> getStats() {
        return ResponseEntity.ok(complaintUseCase.getStats());
    }

    // ── Delete (only NEW or CLASSIFIED) ──────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STUDENT')")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        complaintUseCase.deleteComplaint(id, principal.getId());
        return ResponseEntity.noContent().build();
    }
}

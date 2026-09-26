package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.facility.*;
import com.hostelmind.application.service.FacilityEmergencyUseCase;
import com.hostelmind.domain.model.AssetStatus;
import com.hostelmind.domain.model.SosAlertStatus;
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
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
public class FacilityEmergencyController {

    private final FacilityEmergencyUseCase facilityEmergencyUseCase;

    // ── Facility Assets ──────────────────────────────────────────────────────

    @PostMapping("/assets")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<FacilityAssetDto> createAsset(
            @Valid @RequestBody CreateFacilityAssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facilityEmergencyUseCase.createAsset(request));
    }

    @GetMapping("/assets")
    public ResponseEntity<List<FacilityAssetDto>> getAssets(
            @RequestParam(required = false) UUID hostelId,
            @RequestParam(required = false) AssetStatus status) {
        return ResponseEntity.ok(facilityEmergencyUseCase.getAssets(hostelId, status));
    }

    @PatchMapping("/assets/{assetId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STAFF')")
    public ResponseEntity<FacilityAssetDto> updateAssetStatus(
            @PathVariable UUID assetId,
            @RequestParam AssetStatus status) {
        return ResponseEntity.ok(facilityEmergencyUseCase.updateAssetStatus(assetId, status));
    }

    // ── Maintenance Schedules ────────────────────────────────────────────────

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STAFF')")
    public ResponseEntity<MaintenanceScheduleDto> createMaintenanceSchedule(
            @Valid @RequestBody CreateMaintenanceScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facilityEmergencyUseCase.createMaintenanceSchedule(request));
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_STAFF')")
    public ResponseEntity<List<MaintenanceScheduleDto>> getMaintenanceSchedules() {
        return ResponseEntity.ok(facilityEmergencyUseCase.getMaintenanceSchedules());
    }

    // ── Emergency SOS Alerts ─────────────────────────────────────────────────

    /**
     * Any authenticated user (student, warden, staff) can trigger an SOS.
     */
    @PostMapping("/sos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EmergencySosAlertDto> triggerSos(
            @Valid @RequestBody TriggerSosAlertRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facilityEmergencyUseCase.triggerSosAlert(principal.getId(), request));
    }

    @GetMapping("/sos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<EmergencySosAlertDto>> getAllSosAlerts() {
        return ResponseEntity.ok(facilityEmergencyUseCase.getAllSosAlerts());
    }

    @GetMapping("/sos/active")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<List<EmergencySosAlertDto>> getActiveSosAlerts() {
        return ResponseEntity.ok(facilityEmergencyUseCase.getActiveSosAlerts());
    }

    @PatchMapping("/sos/{alertId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<EmergencySosAlertDto> updateSosStatus(
            @PathVariable UUID alertId,
            @Valid @RequestBody UpdateSosStatusRequest request) {
        return ResponseEntity.ok(
                facilityEmergencyUseCase.updateSosStatus(alertId, request.getStatus()));
    }

    // ── Stats ────────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL')")
    public ResponseEntity<FacilityEmergencyStatsDto> getStats() {
        return ResponseEntity.ok(facilityEmergencyUseCase.getStats());
    }
}

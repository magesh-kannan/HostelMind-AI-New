package com.hostelmind.application.service;

import com.hostelmind.application.dto.facility.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.EmergencySosAlertRepositoryPort;
import com.hostelmind.domain.repository.FacilityAssetRepositoryPort;
import com.hostelmind.domain.repository.MaintenanceScheduleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityEmergencyUseCase {

    private final FacilityAssetRepositoryPort assetRepository;
    private final MaintenanceScheduleRepositoryPort maintenanceRepository;
    private final EmergencySosAlertRepositoryPort sosRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    // ─── Facility Assets ────────────────────────────────────────────────────────

    @Transactional
    public FacilityAssetDto createAsset(CreateFacilityAssetRequest req) {
        FacilityAsset asset = FacilityAsset.builder()
                .hostelId(req.getHostelId())
                .name(req.getName())
                .category(req.getCategory())
                .location(req.getLocation())
                .status(req.getStatus() != null ? req.getStatus() : AssetStatus.OPERATIONAL)
                .lastInspectedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        FacilityAsset saved = assetRepository.save(asset);

        auditLogRepository.save(AuditLog.builder()
                .action("CREATE_FACILITY_ASSET")
                .entityType("FacilityAsset")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("Added asset '%s' (%s) at %s", req.getName(), req.getCategory(), req.getLocation()))
                .createdAt(Instant.now())
                .build());

        return mapToAssetDto(saved);
    }

    public List<FacilityAssetDto> getAssets(UUID hostelId, AssetStatus status) {
        List<FacilityAsset> list;
        if (hostelId != null) {
            list = assetRepository.findByHostelId(hostelId);
        } else if (status != null) {
            list = assetRepository.findByStatus(status);
        } else {
            list = assetRepository.findAll();
        }
        return list.stream().map(this::mapToAssetDto).collect(Collectors.toList());
    }

    @Transactional
    public FacilityAssetDto updateAssetStatus(UUID assetId, AssetStatus status) {
        FacilityAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Facility Asset not found: " + assetId));

        asset.setStatus(status);
        asset.setLastInspectedAt(Instant.now());
        asset.setUpdatedAt(Instant.now());

        FacilityAsset saved = assetRepository.save(asset);

        auditLogRepository.save(AuditLog.builder()
                .action("UPDATE_ASSET_STATUS")
                .entityType("FacilityAsset")
                .entityId(saved.getId().toString())
                .details(String.format("Updated asset '%s' status to %s", saved.getName(), status))
                .createdAt(Instant.now())
                .build());

        return mapToAssetDto(saved);
    }

    // ─── Maintenance Schedules ──────────────────────────────────────────────────

    @Transactional
    public MaintenanceScheduleDto createMaintenanceSchedule(CreateMaintenanceScheduleRequest req) {
        MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                .assetId(req.getAssetId())
                .title(req.getTitle())
                .frequency(req.getFrequency() != null ? req.getFrequency() : MaintenanceFrequency.MONTHLY)
                .assignedTechnician(req.getAssignedTechnician())
                .nextDueDate(req.getNextDueDate())
                .status(MaintenanceStatus.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        MaintenanceSchedule saved = maintenanceRepository.save(schedule);
        return mapToScheduleDto(saved);
    }

    public List<MaintenanceScheduleDto> getMaintenanceSchedules() {
        return maintenanceRepository.findAll().stream()
                .map(this::mapToScheduleDto)
                .collect(Collectors.toList());
    }

    // ─── Emergency SOS Alerts ───────────────────────────────────────────────────

    @Transactional
    public EmergencySosAlertDto triggerSosAlert(UUID studentId, TriggerSosAlertRequest req) {
        EmergencySosAlert alert = EmergencySosAlert.builder()
                .studentId(studentId)
                .hostelId(req.getHostelId())
                .roomNumber(req.getRoomNumber())
                .sosType(req.getSosType())
                .locationDetails(req.getLocationDetails())
                .status(SosAlertStatus.TRIGGERED)
                .triggeredAt(Instant.now())
                .build();

        EmergencySosAlert saved = sosRepository.save(alert);

        auditLogRepository.save(AuditLog.builder()
                .userId(studentId)
                .action("TRIGGER_EMERGENCY_SOS")
                .entityType("EmergencySosAlert")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("HIGH URGENCY SOS ALERT: %s at %s (Room %s)", req.getSosType(), req.getLocationDetails(), req.getRoomNumber()))
                .createdAt(Instant.now())
                .build());

        return mapToSosDto(saved);
    }

    @Transactional
    public EmergencySosAlertDto updateSosStatus(UUID alertId, SosAlertStatus targetStatus) {
        EmergencySosAlert alert = sosRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("SOS Alert not found: " + alertId));

        alert.transitionTo(targetStatus);
        EmergencySosAlert saved = sosRepository.save(alert);

        auditLogRepository.save(AuditLog.builder()
                .action("UPDATE_SOS_STATUS")
                .entityType("EmergencySosAlert")
                .entityId(saved.getId().toString())
                .details(String.format("Updated SOS alert status to %s", targetStatus))
                .createdAt(Instant.now())
                .build());

        return mapToSosDto(saved);
    }

    public List<EmergencySosAlertDto> getAllSosAlerts() {
        return sosRepository.findAll().stream()
                .map(this::mapToSosDto)
                .collect(Collectors.toList());
    }

    public List<EmergencySosAlertDto> getActiveSosAlerts() {
        return sosRepository.findByStatus(SosAlertStatus.TRIGGERED).stream()
                .map(this::mapToSosDto)
                .collect(Collectors.toList());
    }

    // ─── Stats ──────────────────────────────────────────────────────────────────

    public FacilityEmergencyStatsDto getStats() {
        List<FacilityAsset> assets = assetRepository.findAll();
        List<EmergencySosAlert> alerts = sosRepository.findAll();

        long operational = assets.stream().filter(a -> a.getStatus() == AssetStatus.OPERATIONAL).count();
        long maintenance = assets.stream().filter(a -> a.getStatus() == AssetStatus.UNDER_MAINTENANCE).count();
        long activeSos = alerts.stream().filter(a -> a.getStatus() == SosAlertStatus.TRIGGERED || a.getStatus() == SosAlertStatus.ACKNOWLEDGED).count();
        long resolvedSos = alerts.stream().filter(a -> a.getStatus() == SosAlertStatus.RESOLVED).count();

        return FacilityEmergencyStatsDto.builder()
                .totalAssetsCount(assets.size())
                .operationalAssetsCount(operational)
                .underMaintenanceCount(maintenance)
                .activeSosAlertsCount(activeSos)
                .resolvedSosAlertsCount(resolvedSos)
                .build();
    }

    // ─── Mapping Helpers ───────────────────────────────────────────────────────

    private FacilityAssetDto mapToAssetDto(FacilityAsset a) {
        return FacilityAssetDto.builder()
                .id(a.getId())
                .hostelId(a.getHostelId())
                .name(a.getName())
                .category(a.getCategory())
                .location(a.getLocation())
                .status(a.getStatus())
                .lastInspectedAt(a.getLastInspectedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    private MaintenanceScheduleDto mapToScheduleDto(MaintenanceSchedule m) {
        String assetName = assetRepository.findById(m.getAssetId())
                .map(FacilityAsset::getName)
                .orElse("Unknown Asset");

        return MaintenanceScheduleDto.builder()
                .id(m.getId())
                .assetId(m.getAssetId())
                .assetName(assetName)
                .title(m.getTitle())
                .frequency(m.getFrequency())
                .assignedTechnician(m.getAssignedTechnician())
                .nextDueDate(m.getNextDueDate())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private EmergencySosAlertDto mapToSosDto(EmergencySosAlert s) {
        return EmergencySosAlertDto.builder()
                .id(s.getId())
                .studentId(s.getStudentId())
                .hostelId(s.getHostelId())
                .roomNumber(s.getRoomNumber())
                .sosType(s.getSosType())
                .locationDetails(s.getLocationDetails())
                .status(s.getStatus())
                .triggeredAt(s.getTriggeredAt())
                .resolvedAt(s.getResolvedAt())
                .build();
    }
}

package com.hostelmind.application.service;

import com.hostelmind.application.dto.facility.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.EmergencySosAlertRepositoryPort;
import com.hostelmind.domain.repository.FacilityAssetRepositoryPort;
import com.hostelmind.domain.repository.MaintenanceScheduleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityEmergencyUseCaseTest {

    @Mock
    private FacilityAssetRepositoryPort assetRepository;

    @Mock
    private MaintenanceScheduleRepositoryPort maintenanceRepository;

    @Mock
    private EmergencySosAlertRepositoryPort sosRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private FacilityEmergencyUseCase useCase;

    private UUID hostelId;
    private UUID studentId;
    private UUID assetId;
    private UUID alertId;

    @BeforeEach
    void setUp() {
        hostelId  = UUID.randomUUID();
        studentId = UUID.randomUUID();
        assetId   = UUID.randomUUID();
        alertId   = UUID.randomUUID();
    }

    // ── Facility Assets ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create a new facility asset and emit audit log")
    void createAsset_Success() {
        CreateFacilityAssetRequest req = new CreateFacilityAssetRequest(
                hostelId, "Generator Set A", AssetCategory.GENERATOR,
                "Block-B, Ground Floor", AssetStatus.OPERATIONAL
        );

        when(assetRepository.save(any())).thenAnswer(inv -> {
            FacilityAsset a = inv.getArgument(0);
            if (a.getId() == null) a.setId(UUID.randomUUID());
            return a;
        });

        FacilityAssetDto dto = useCase.createAsset(req);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("Generator Set A", dto.getName());
        assertEquals(AssetCategory.GENERATOR, dto.getCategory());
        assertEquals(AssetStatus.OPERATIONAL, dto.getStatus());

        verify(assetRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should default status to OPERATIONAL when not provided")
    void createAsset_DefaultStatus() {
        CreateFacilityAssetRequest req = new CreateFacilityAssetRequest(
                hostelId, "Water Pump", AssetCategory.WASHROOM,
                "Rooftop", null
        );

        when(assetRepository.save(any())).thenAnswer(inv -> {
            FacilityAsset a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        FacilityAssetDto dto = useCase.createAsset(req);

        assertEquals(AssetStatus.OPERATIONAL, dto.getStatus());
    }

    @Test
    @DisplayName("Should list all assets when no filter applied")
    void getAssets_All() {
        FacilityAsset asset = FacilityAsset.builder()
                .id(assetId).hostelId(hostelId)
                .name("Lift-1").category(AssetCategory.ELEVATOR)
                .location("West Wing").status(AssetStatus.OPERATIONAL)
                .lastInspectedAt(Instant.now()).createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(assetRepository.findAll()).thenReturn(List.of(asset));

        List<FacilityAssetDto> list = useCase.getAssets(null, null);

        assertEquals(1, list.size());
        assertEquals("Lift-1", list.get(0).getName());
        verify(assetRepository).findAll();
    }

    @Test
    @DisplayName("Should filter assets by hostel when hostelId provided")
    void getAssets_ByHostel() {
        when(assetRepository.findByHostelId(hostelId)).thenReturn(List.of());
        List<FacilityAssetDto> list = useCase.getAssets(hostelId, null);
        assertNotNull(list);
        verify(assetRepository).findByHostelId(hostelId);
    }

    @Test
    @DisplayName("Should update asset status and emit audit log")
    void updateAssetStatus_Success() {
        FacilityAsset existing = FacilityAsset.builder()
                .id(assetId).hostelId(hostelId)
                .name("CCTV Block-A").category(AssetCategory.SECURITY_CAMERA)
                .location("Block-A Entrance").status(AssetStatus.OPERATIONAL)
                .lastInspectedAt(Instant.now()).createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(existing));
        when(assetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FacilityAssetDto dto = useCase.updateAssetStatus(assetId, AssetStatus.UNDER_MAINTENANCE);

        assertEquals(AssetStatus.UNDER_MAINTENANCE, dto.getStatus());
        verify(assetRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should throw when updating status of non-existent asset")
    void updateAssetStatus_NotFound() {
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> useCase.updateAssetStatus(assetId, AssetStatus.OUT_OF_SERVICE));
    }

    // ── Maintenance Schedules ────────────────────────────────────────────────

    @Test
    @DisplayName("Should create maintenance schedule for an asset")
    void createMaintenanceSchedule_Success() {
        CreateMaintenanceScheduleRequest req = new CreateMaintenanceScheduleRequest(
                assetId, "Monthly Generator Service",
                MaintenanceFrequency.MONTHLY, "Tech Ravi",
                LocalDate.now().plusDays(30)
        );

        when(maintenanceRepository.save(any())).thenAnswer(inv -> {
            MaintenanceSchedule m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        // asset lookup for name enrichment in mapToScheduleDto
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(
                FacilityAsset.builder().id(assetId).name("Generator Set A").build()
        ));

        MaintenanceScheduleDto dto = useCase.createMaintenanceSchedule(req);

        assertNotNull(dto);
        assertEquals("Monthly Generator Service", dto.getTitle());
        assertEquals(MaintenanceFrequency.MONTHLY, dto.getFrequency());
        assertEquals(MaintenanceStatus.PENDING, dto.getStatus());
        assertEquals("Generator Set A", dto.getAssetName());
        verify(maintenanceRepository).save(any());
    }

    // ── SOS Alerts ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should trigger an SOS alert and emit high-urgency audit log")
    void triggerSosAlert_Success() {
        TriggerSosAlertRequest req = new TriggerSosAlertRequest(
                hostelId, "301", SosType.MEDICAL,
                "Girls Hostel, Floor 3, Near washroom"
        );

        when(sosRepository.save(any())).thenAnswer(inv -> {
            EmergencySosAlert a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        EmergencySosAlertDto dto = useCase.triggerSosAlert(studentId, req);

        assertNotNull(dto.getId());
        assertEquals(SosType.MEDICAL, dto.getSosType());
        assertEquals(SosAlertStatus.TRIGGERED, dto.getStatus());
        assertEquals(studentId, dto.getStudentId());

        verify(sosRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should transition SOS alert from TRIGGERED to ACKNOWLEDGED")
    void updateSosStatus_TriggeredToAcknowledged_Success() {
        EmergencySosAlert existing = EmergencySosAlert.builder()
                .id(alertId).studentId(studentId)
                .hostelId(hostelId).roomNumber("202")
                .sosType(SosType.FIRE)
                .locationDetails("Block C, 2nd Floor")
                .status(SosAlertStatus.TRIGGERED)
                .triggeredAt(Instant.now())
                .build();

        when(sosRepository.findById(alertId)).thenReturn(Optional.of(existing));
        when(sosRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmergencySosAlertDto dto = useCase.updateSosStatus(alertId, SosAlertStatus.ACKNOWLEDGED);

        assertEquals(SosAlertStatus.ACKNOWLEDGED, dto.getStatus());
        verify(sosRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should transition SOS from ACKNOWLEDGED to RESOLVED and stamp resolvedAt")
    void updateSosStatus_AcknowledgedToResolved_Success() {
        EmergencySosAlert existing = EmergencySosAlert.builder()
                .id(alertId).studentId(studentId)
                .hostelId(hostelId).roomNumber("104")
                .sosType(SosType.SECURITY)
                .locationDetails("Boys Hostel Gate")
                .status(SosAlertStatus.ACKNOWLEDGED)
                .triggeredAt(Instant.now().minusSeconds(300))
                .build();

        when(sosRepository.findById(alertId)).thenReturn(Optional.of(existing));
        when(sosRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EmergencySosAlertDto dto = useCase.updateSosStatus(alertId, SosAlertStatus.RESOLVED);

        assertEquals(SosAlertStatus.RESOLVED, dto.getStatus());
        assertNotNull(dto.getResolvedAt());
    }

    @Test
    @DisplayName("Should reject invalid SOS status transition (RESOLVED -> TRIGGERED)")
    void updateSosStatus_InvalidTransition_ThrowsException() {
        EmergencySosAlert resolved = EmergencySosAlert.builder()
                .id(alertId).studentId(studentId)
                .hostelId(hostelId).roomNumber("101")
                .sosType(SosType.MEDICAL)
                .locationDetails("Block A")
                .status(SosAlertStatus.RESOLVED)
                .triggeredAt(Instant.now().minusSeconds(600))
                .resolvedAt(Instant.now().minusSeconds(60))
                .build();

        when(sosRepository.findById(alertId)).thenReturn(Optional.of(resolved));

        assertThrows(IllegalStateException.class,
                () -> useCase.updateSosStatus(alertId, SosAlertStatus.TRIGGERED));
    }

    @Test
    @DisplayName("Should throw when updating status of non-existent SOS alert")
    void updateSosStatus_NotFound() {
        when(sosRepository.findById(alertId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> useCase.updateSosStatus(alertId, SosAlertStatus.ACKNOWLEDGED));
    }

    // ── Stats ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return accurate facility and SOS stats")
    void getStats_Success() {
        List<FacilityAsset> assets = List.of(
                FacilityAsset.builder().status(AssetStatus.OPERATIONAL).build(),
                FacilityAsset.builder().status(AssetStatus.OPERATIONAL).build(),
                FacilityAsset.builder().status(AssetStatus.UNDER_MAINTENANCE).build(),
                FacilityAsset.builder().status(AssetStatus.OUT_OF_SERVICE).build()
        );

        List<EmergencySosAlert> alerts = List.of(
                EmergencySosAlert.builder().status(SosAlertStatus.TRIGGERED).build(),
                EmergencySosAlert.builder().status(SosAlertStatus.ACKNOWLEDGED).build(),
                EmergencySosAlert.builder().status(SosAlertStatus.RESOLVED).build()
        );

        when(assetRepository.findAll()).thenReturn(assets);
        when(sosRepository.findAll()).thenReturn(alerts);

        FacilityEmergencyStatsDto stats = useCase.getStats();

        assertNotNull(stats);
        assertEquals(4, stats.getTotalAssetsCount());
        assertEquals(2, stats.getOperationalAssetsCount());
        assertEquals(1, stats.getUnderMaintenanceCount());
        assertEquals(2, stats.getActiveSosAlertsCount());   // TRIGGERED + ACKNOWLEDGED
        assertEquals(1, stats.getResolvedSosAlertsCount());
    }
}

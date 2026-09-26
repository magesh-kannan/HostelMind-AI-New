package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.MaintenanceFrequency;
import com.hostelmind.domain.model.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceScheduleDto {
    private UUID id;
    private UUID assetId;
    private String assetName;
    private String title;
    private MaintenanceFrequency frequency;
    private String assignedTechnician;
    private LocalDate nextDueDate;
    private MaintenanceStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

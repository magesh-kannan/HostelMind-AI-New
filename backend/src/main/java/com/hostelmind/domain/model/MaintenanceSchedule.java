package com.hostelmind.domain.model;

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
public class MaintenanceSchedule {
    private UUID id;
    private UUID assetId;
    private String title;
    private MaintenanceFrequency frequency;
    private String assignedTechnician;
    private LocalDate nextDueDate;
    private MaintenanceStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

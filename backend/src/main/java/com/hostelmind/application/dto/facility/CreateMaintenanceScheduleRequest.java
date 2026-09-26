package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.MaintenanceFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaintenanceScheduleRequest {
    @NotNull(message = "Asset ID is required")
    private UUID assetId;

    @NotBlank(message = "Title is required")
    private String title;

    private MaintenanceFrequency frequency = MaintenanceFrequency.MONTHLY;
    private String assignedTechnician;

    @NotNull(message = "Next due date is required")
    private LocalDate nextDueDate;
}

package com.hostelmind.application.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityEmergencyStatsDto {
    private long totalAssetsCount;
    private long operationalAssetsCount;
    private long underMaintenanceCount;
    private long activeSosAlertsCount;
    private long resolvedSosAlertsCount;
}

package com.hostelmind.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyStatsDto {
    private long totalHostels;
    private long totalRooms;
    private long totalCapacity;
    private long occupiedBeds;
    private long vacantBeds;
    private double occupancyPercentage;
}

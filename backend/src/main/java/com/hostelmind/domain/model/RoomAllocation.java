package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAllocation {
    private UUID id;
    private UUID studentId;
    private UUID bedId;
    private UUID roomId;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private AllocationStatus status;
    private UUID allocatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}

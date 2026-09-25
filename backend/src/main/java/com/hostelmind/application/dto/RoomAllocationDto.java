package com.hostelmind.application.dto;

import com.hostelmind.domain.model.AllocationStatus;
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
public class RoomAllocationDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private UUID bedId;
    private String bedNumber;
    private UUID roomId;
    private String roomNumber;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private AllocationStatus status;
    private UUID allocatedBy;
    private Instant createdAt;
}

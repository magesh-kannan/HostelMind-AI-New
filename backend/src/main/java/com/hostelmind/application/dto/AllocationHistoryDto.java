package com.hostelmind.application.dto;

import com.hostelmind.domain.model.AllocationActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationHistoryDto {
    private UUID id;
    private UUID allocationId;
    private UUID studentId;
    private AllocationActionType actionType;
    private UUID fromBedId;
    private UUID toBedId;
    private String reason;
    private UUID performedBy;
    private Instant createdAt;
}

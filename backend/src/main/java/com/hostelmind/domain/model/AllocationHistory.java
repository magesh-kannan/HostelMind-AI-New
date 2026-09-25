package com.hostelmind.domain.model;

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
public class AllocationHistory {
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

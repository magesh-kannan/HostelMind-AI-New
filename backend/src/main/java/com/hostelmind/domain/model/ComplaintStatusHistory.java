package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable audit record of every complaint state transition.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintStatusHistory {
    private UUID id;
    private UUID complaintId;
    private UUID changedByUserId;

    private ComplaintStatus fromStatus;
    private ComplaintStatus toStatus;
    private String note;       // optional human comment on the transition

    private Instant changedAt;
}

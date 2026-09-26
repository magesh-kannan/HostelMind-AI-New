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
public class Complaint {
    private UUID id;
    private UUID studentId;
    private UUID hostelId;
    private UUID roomId;          // optional – room context
    private UUID assignedToId;    // warden / staff assigned

    private String title;
    private String description;

    private ComplaintCategory category;
    private ComplaintPriority priority;
    private ComplaintStatus status;

    // File attachment URL (stored via FileStoragePort)
    private String attachmentUrl;

    // SLA deadline (set during PRIORITIZED transition)
    private Instant slaDeadline;

    // Reopen tracking
    private int reopenCount;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private Instant closedAt;

    // ──────────────────────────────────────────────────────────────
    // Domain logic: state machine transitions
    // ──────────────────────────────────────────────────────────────

    /**
     * Allowed transitions map (from -> allowed targets).
     * Enforced in the use-case layer via canTransitionTo().
     */
    public boolean canTransitionTo(ComplaintStatus target) {
        if (this.status == null) return false;
        return switch (this.status) {
            case NEW                -> target == ComplaintStatus.CLASSIFIED;
            case CLASSIFIED         -> target == ComplaintStatus.PRIORITIZED;
            case PRIORITIZED        -> target == ComplaintStatus.ASSIGNED;
            case ASSIGNED           -> target == ComplaintStatus.IN_PROGRESS;
            case IN_PROGRESS        -> target == ComplaintStatus.WAITING_FOR_STUDENT
                                    || target == ComplaintStatus.RESOLVED;
            case WAITING_FOR_STUDENT-> target == ComplaintStatus.IN_PROGRESS
                                    || target == ComplaintStatus.RESOLVED;
            case RESOLVED           -> target == ComplaintStatus.VERIFIED
                                    || target == ComplaintStatus.REOPENED;
            case VERIFIED           -> target == ComplaintStatus.CLOSED
                                    || target == ComplaintStatus.REOPENED;
            case CLOSED             -> target == ComplaintStatus.REOPENED;
            case REOPENED           -> target == ComplaintStatus.CLASSIFIED;
        };
    }
}

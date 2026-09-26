package com.hostelmind.domain.model;

/**
 * Complaint lifecycle state machine.
 * Transitions:
 *   NEW -> CLASSIFIED -> PRIORITIZED -> ASSIGNED -> IN_PROGRESS
 *     -> WAITING_FOR_STUDENT -> IN_PROGRESS (reopen)
 *     -> IN_PROGRESS -> RESOLVED -> VERIFIED -> CLOSED
 *   RESOLVED/VERIFIED/CLOSED -> REOPENED -> CLASSIFIED (reopening rule)
 */
public enum ComplaintStatus {
    NEW,
    CLASSIFIED,
    PRIORITIZED,
    ASSIGNED,
    IN_PROGRESS,
    WAITING_FOR_STUDENT,
    RESOLVED,
    VERIFIED,
    CLOSED,
    REOPENED
}

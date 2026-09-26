package com.hostelmind.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintStatsDto {
    private long totalComplaints;
    private long newComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long closedComplaints;
    private long overdueComplaints;  // past SLA deadline, not yet resolved
}

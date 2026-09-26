package com.hostelmind.application.dto;

import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintPriority;
import com.hostelmind.domain.model.ComplaintStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ComplaintDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID hostelId;
    private UUID roomId;
    private UUID assignedToId;
    private String assignedToName;

    private String title;
    private String description;

    private ComplaintCategory category;
    private ComplaintPriority priority;
    private ComplaintStatus status;

    private String attachmentUrl;
    private Instant slaDeadline;
    private int reopenCount;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private Instant closedAt;
}

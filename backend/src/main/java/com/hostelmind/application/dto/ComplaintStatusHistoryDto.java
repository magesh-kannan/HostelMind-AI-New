package com.hostelmind.application.dto;

import com.hostelmind.domain.model.ComplaintStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ComplaintStatusHistoryDto {
    private UUID id;
    private UUID complaintId;
    private UUID changedByUserId;
    private String changedByUserName;
    private ComplaintStatus fromStatus;
    private ComplaintStatus toStatus;
    private String note;
    private Instant changedAt;
}

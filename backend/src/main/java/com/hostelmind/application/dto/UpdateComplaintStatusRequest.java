package com.hostelmind.application.dto;

import com.hostelmind.domain.model.ComplaintPriority;
import com.hostelmind.domain.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UpdateComplaintStatusRequest {

    @NotNull(message = "status is required")
    private ComplaintStatus status;

    private UUID assignedToId;        // required when transitioning to ASSIGNED
    private ComplaintPriority priority; // can be set during PRIORITIZED step
    private Instant slaDeadline;       // can be set during PRIORITIZED step
    private String note;               // optional warden comment
}

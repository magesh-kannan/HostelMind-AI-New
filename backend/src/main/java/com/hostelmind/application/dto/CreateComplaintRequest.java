package com.hostelmind.application.dto;

import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateComplaintRequest {

    @NotNull(message = "hostelId is required")
    private UUID hostelId;

    private UUID roomId;  // optional

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be ≤ 255 characters")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "category is required")
    private ComplaintCategory category;

    // Default priority set in use case; can be overridden by warden later
    private ComplaintPriority priority;

    // Optionally upload attachment before/after creation
    private String attachmentUrl;
}

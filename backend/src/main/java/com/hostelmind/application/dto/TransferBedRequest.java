package com.hostelmind.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferBedRequest {
    @NotNull(message = "Allocation ID is required")
    private UUID allocationId;

    @NotNull(message = "New target Bed ID is required")
    private UUID newBedId;

    @NotBlank(message = "Reason for transfer is required")
    private String reason;
}

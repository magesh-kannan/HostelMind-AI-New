package com.hostelmind.application.dto;

import jakarta.validation.constraints.Min;
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
public class CreateBlockRequest {
    @NotNull(message = "Hostel ID is required")
    private UUID hostelId;

    @NotBlank(message = "Block name is required")
    private String name;

    @NotBlank(message = "Block code is required")
    private String code;

    @Min(value = 1, message = "Total floors must be at least 1")
    private int totalFloors;
}

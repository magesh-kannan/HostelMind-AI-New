package com.hostelmind.application.dto;

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
public class CreateFloorRequest {
    @NotNull(message = "Block ID is required")
    private UUID blockId;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    private String name;
}

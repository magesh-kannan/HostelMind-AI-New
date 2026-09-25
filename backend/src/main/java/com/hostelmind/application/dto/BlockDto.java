package com.hostelmind.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockDto {
    private UUID id;
    private UUID hostelId;
    private String name;
    private String code;
    private int totalFloors;
}

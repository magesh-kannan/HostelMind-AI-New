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
public class FloorDto {
    private UUID id;
    private UUID blockId;
    private int floorNumber;
    private String name;
}

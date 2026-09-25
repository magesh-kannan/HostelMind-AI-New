package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Floor {
    private UUID id;
    private UUID blockId;
    private int floorNumber;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}

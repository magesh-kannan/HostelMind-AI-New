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
public class Bed {
    private UUID id;
    private UUID roomId;
    private String bedNumber;
    private BedStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

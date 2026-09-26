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
public class FacilityAsset {
    private UUID id;
    private UUID hostelId;
    private String name;
    private AssetCategory category;
    private String location;
    private AssetStatus status;
    private Instant lastInspectedAt;
    private Instant createdAt;
    private Instant updatedAt;
}

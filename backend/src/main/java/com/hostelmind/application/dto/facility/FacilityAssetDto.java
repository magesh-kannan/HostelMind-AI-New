package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.AssetCategory;
import com.hostelmind.domain.model.AssetStatus;
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
public class FacilityAssetDto {
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

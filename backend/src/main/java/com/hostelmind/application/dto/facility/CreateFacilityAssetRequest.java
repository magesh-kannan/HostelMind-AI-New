package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.AssetCategory;
import com.hostelmind.domain.model.AssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacilityAssetRequest {
    @NotNull(message = "Hostel ID is required")
    private UUID hostelId;

    @NotBlank(message = "Asset name is required")
    private String name;

    @NotNull(message = "Category is required")
    private AssetCategory category;

    @NotBlank(message = "Location is required")
    private String location;

    private AssetStatus status = AssetStatus.OPERATIONAL;
}

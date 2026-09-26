package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.SosType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerSosAlertRequest {
    private UUID hostelId;
    private String roomNumber;

    @NotNull(message = "SOS Type is required")
    private SosType sosType;

    @NotBlank(message = "Location details are required")
    private String locationDetails;
}

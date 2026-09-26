package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.SosAlertStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSosStatusRequest {
    @NotNull(message = "Status is required")
    private SosAlertStatus status;
}

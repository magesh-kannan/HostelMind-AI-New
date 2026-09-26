package com.hostelmind.application.dto.facility;

import com.hostelmind.domain.model.SosAlertStatus;
import com.hostelmind.domain.model.SosType;
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
public class EmergencySosAlertDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID hostelId;
    private String roomNumber;
    private SosType sosType;
    private String locationDetails;
    private SosAlertStatus status;
    private Instant triggeredAt;
    private Instant resolvedAt;
}

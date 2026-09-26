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
public class EmergencySosAlert {
    private UUID id;
    private UUID studentId;
    private UUID hostelId;
    private String roomNumber;
    private SosType sosType;
    private String locationDetails;
    private SosAlertStatus status;
    private Instant triggeredAt;
    private Instant resolvedAt;

    public void transitionTo(SosAlertStatus targetStatus) {
        if (!this.status.canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                String.format("Invalid SOS alert transition from %s to %s", this.status, targetStatus)
            );
        }
        this.status = targetStatus;
        if (targetStatus == SosAlertStatus.RESOLVED || targetStatus == SosAlertStatus.FALSE_ALARM) {
            this.resolvedAt = Instant.now();
        }
    }
}

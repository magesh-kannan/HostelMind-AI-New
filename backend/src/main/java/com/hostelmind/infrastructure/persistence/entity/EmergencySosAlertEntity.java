package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.SosAlertStatus;
import com.hostelmind.domain.model.SosType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "emergency_sos_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencySosAlertEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "hostel_id")
    private UUID hostelId;

    @Column(name = "room_number")
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "sos_type", nullable = false)
    private SosType sosType;

    @Column(name = "location_details", nullable = false)
    private String locationDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SosAlertStatus status;

    @Column(name = "triggered_at", nullable = false, updatable = false)
    private Instant triggeredAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @PrePersist
    protected void onCreate() {
        if (triggeredAt == null) triggeredAt = Instant.now();
        if (status == null) status = SosAlertStatus.TRIGGERED;
    }
}

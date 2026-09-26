package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.MealAttendanceStatus;
import com.hostelmind.domain.model.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meal_attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "meal_type", "attendance_date"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealAttendanceEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "hostel_id")
    private UUID hostelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "qr_token", nullable = false, unique = true)
    private String qrToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealAttendanceStatus status;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private Instant scannedAt;

    @PrePersist
    protected void onCreate() {
        if (scannedAt == null) scannedAt = Instant.now();
        if (status == null) status = MealAttendanceStatus.SERVED;
    }
}

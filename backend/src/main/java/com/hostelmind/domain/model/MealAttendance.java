package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealAttendance {
    private UUID id;
    private UUID studentId;
    private UUID hostelId;
    private MealType mealType;
    private LocalDate attendanceDate;
    private String qrToken;
    private MealAttendanceStatus status;
    private Instant scannedAt;
}

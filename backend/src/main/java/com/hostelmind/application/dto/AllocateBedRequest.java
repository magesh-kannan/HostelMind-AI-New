package com.hostelmind.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateBedRequest {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    @NotNull(message = "Bed ID is required")
    private UUID bedId;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;
}

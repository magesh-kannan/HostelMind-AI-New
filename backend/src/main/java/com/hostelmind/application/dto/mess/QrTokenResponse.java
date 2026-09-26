package com.hostelmind.application.dto.mess;

import com.hostelmind.domain.model.MealType;
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
public class QrTokenResponse {
    private String qrToken;
    private UUID studentId;
    private MealType mealType;
    private LocalDate date;
    private Instant expiresAt;
}

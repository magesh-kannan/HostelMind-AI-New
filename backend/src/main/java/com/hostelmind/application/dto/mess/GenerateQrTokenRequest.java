package com.hostelmind.application.dto.mess;

import com.hostelmind.domain.model.MealType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQrTokenRequest {
    @NotNull(message = "Meal type is required")
    private MealType mealType;

    private LocalDate date;
}

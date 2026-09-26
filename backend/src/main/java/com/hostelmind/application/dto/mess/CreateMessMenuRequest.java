package com.hostelmind.application.dto.mess;

import com.hostelmind.domain.model.DayOfWeek;
import com.hostelmind.domain.model.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessMenuRequest {
    @NotNull(message = "Hostel ID is required")
    private UUID hostelId;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotBlank(message = "Menu items are required")
    private String items;

    private Integer calorieCount;
    private String specialNotes;
}

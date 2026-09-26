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
public class MessMenu {
    private UUID id;
    private UUID hostelId;
    private DayOfWeek dayOfWeek;
    private MealType mealType;
    private String items;
    private Integer calorieCount;
    private String specialNotes;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}

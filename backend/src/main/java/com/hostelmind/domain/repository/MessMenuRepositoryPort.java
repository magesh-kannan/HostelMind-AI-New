package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.DayOfWeek;
import com.hostelmind.domain.model.MealType;
import com.hostelmind.domain.model.MessMenu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessMenuRepositoryPort {
    MessMenu save(MessMenu menu);
    Optional<MessMenu> findById(UUID id);
    Optional<MessMenu> findByHostelDayAndMeal(UUID hostelId, DayOfWeek dayOfWeek, MealType mealType);
    List<MessMenu> findByHostelId(UUID hostelId);
    List<MessMenu> findAll();
    void deleteById(UUID id);
}

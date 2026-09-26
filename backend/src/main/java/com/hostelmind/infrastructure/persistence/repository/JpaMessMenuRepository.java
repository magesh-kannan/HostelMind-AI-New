package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.DayOfWeek;
import com.hostelmind.domain.model.MealType;
import com.hostelmind.infrastructure.persistence.entity.MessMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMessMenuRepository extends JpaRepository<MessMenuEntity, UUID> {
    Optional<MessMenuEntity> findByHostelIdAndDayOfWeekAndMealType(UUID hostelId, DayOfWeek dayOfWeek, MealType mealType);
    List<MessMenuEntity> findByHostelId(UUID hostelId);
}

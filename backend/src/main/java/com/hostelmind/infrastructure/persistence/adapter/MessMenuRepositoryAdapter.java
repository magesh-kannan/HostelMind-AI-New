package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.DayOfWeek;
import com.hostelmind.domain.model.MealType;
import com.hostelmind.domain.model.MessMenu;
import com.hostelmind.domain.repository.MessMenuRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.MessMenuEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaMessMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessMenuRepositoryAdapter implements MessMenuRepositoryPort {

    private final JpaMessMenuRepository jpaRepository;

    @Override
    public MessMenu save(MessMenu domain) {
        MessMenuEntity entity = toEntity(domain);
        MessMenuEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MessMenu> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MessMenu> findByHostelDayAndMeal(UUID hostelId, DayOfWeek dayOfWeek, MealType mealType) {
        return jpaRepository.findByHostelIdAndDayOfWeekAndMealType(hostelId, dayOfWeek, mealType).map(this::toDomain);
    }

    @Override
    public List<MessMenu> findByHostelId(UUID hostelId) {
        return jpaRepository.findByHostelId(hostelId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MessMenu> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private MessMenuEntity toEntity(MessMenu domain) {
        return MessMenuEntity.builder()
                .id(domain.getId())
                .hostelId(domain.getHostelId())
                .dayOfWeek(domain.getDayOfWeek())
                .mealType(domain.getMealType())
                .items(domain.getItems())
                .calorieCount(domain.getCalorieCount())
                .specialNotes(domain.getSpecialNotes())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private MessMenu toDomain(MessMenuEntity entity) {
        return MessMenu.builder()
                .id(entity.getId())
                .hostelId(entity.getHostelId())
                .dayOfWeek(entity.getDayOfWeek())
                .mealType(entity.getMealType())
                .items(entity.getItems())
                .calorieCount(entity.getCalorieCount())
                .specialNotes(entity.getSpecialNotes())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

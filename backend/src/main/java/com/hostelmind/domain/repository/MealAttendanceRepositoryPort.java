package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.MealAttendance;
import com.hostelmind.domain.model.MealType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealAttendanceRepositoryPort {
    MealAttendance save(MealAttendance attendance);
    Optional<MealAttendance> findById(UUID id);
    Optional<MealAttendance> findByQrToken(String qrToken);
    Optional<MealAttendance> findByStudentMealAndDate(UUID studentId, MealType mealType, LocalDate date);
    List<MealAttendance> findByStudentId(UUID studentId);
    List<MealAttendance> findByDate(LocalDate date);
    List<MealAttendance> findAll();
}

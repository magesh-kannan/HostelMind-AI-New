package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.MealType;
import com.hostelmind.infrastructure.persistence.entity.MealAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMealAttendanceRepository extends JpaRepository<MealAttendanceEntity, UUID> {
    Optional<MealAttendanceEntity> findByQrToken(String qrToken);
    Optional<MealAttendanceEntity> findByStudentIdAndMealTypeAndAttendanceDate(UUID studentId, MealType mealType, LocalDate attendanceDate);
    List<MealAttendanceEntity> findByStudentId(UUID studentId);
    List<MealAttendanceEntity> findByAttendanceDate(LocalDate attendanceDate);
}

package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.MealAttendance;
import com.hostelmind.domain.model.MealType;
import com.hostelmind.domain.repository.MealAttendanceRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.MealAttendanceEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaMealAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MealAttendanceRepositoryAdapter implements MealAttendanceRepositoryPort {

    private final JpaMealAttendanceRepository jpaRepository;

    @Override
    public MealAttendance save(MealAttendance domain) {
        MealAttendanceEntity entity = toEntity(domain);
        MealAttendanceEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MealAttendance> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MealAttendance> findByQrToken(String qrToken) {
        return jpaRepository.findByQrToken(qrToken).map(this::toDomain);
    }

    @Override
    public Optional<MealAttendance> findByStudentMealAndDate(UUID studentId, MealType mealType, LocalDate date) {
        return jpaRepository.findByStudentIdAndMealTypeAndAttendanceDate(studentId, mealType, date).map(this::toDomain);
    }

    @Override
    public List<MealAttendance> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MealAttendance> findByDate(LocalDate date) {
        return jpaRepository.findByAttendanceDate(date).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MealAttendance> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private MealAttendanceEntity toEntity(MealAttendance domain) {
        return MealAttendanceEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .hostelId(domain.getHostelId())
                .mealType(domain.getMealType())
                .attendanceDate(domain.getAttendanceDate())
                .qrToken(domain.getQrToken())
                .status(domain.getStatus())
                .scannedAt(domain.getScannedAt())
                .build();
    }

    private MealAttendance toDomain(MealAttendanceEntity entity) {
        return MealAttendance.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .hostelId(entity.getHostelId())
                .mealType(entity.getMealType())
                .attendanceDate(entity.getAttendanceDate())
                .qrToken(entity.getQrToken())
                .status(entity.getStatus())
                .scannedAt(entity.getScannedAt())
                .build();
    }
}

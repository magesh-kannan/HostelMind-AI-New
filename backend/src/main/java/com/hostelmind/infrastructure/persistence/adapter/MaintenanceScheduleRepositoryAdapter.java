package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.MaintenanceSchedule;
import com.hostelmind.domain.model.MaintenanceStatus;
import com.hostelmind.domain.repository.MaintenanceScheduleRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.MaintenanceScheduleEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaMaintenanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MaintenanceScheduleRepositoryAdapter implements MaintenanceScheduleRepositoryPort {

    private final JpaMaintenanceScheduleRepository jpaRepository;

    @Override
    public MaintenanceSchedule save(MaintenanceSchedule domain) {
        MaintenanceScheduleEntity entity = toEntity(domain);
        MaintenanceScheduleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MaintenanceSchedule> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MaintenanceSchedule> findByAssetId(UUID assetId) {
        return jpaRepository.findByAssetId(assetId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceSchedule> findByStatus(MaintenanceStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceSchedule> findDueBefore(LocalDate date) {
        return jpaRepository.findDueBefore(date).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceSchedule> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private MaintenanceScheduleEntity toEntity(MaintenanceSchedule domain) {
        return MaintenanceScheduleEntity.builder()
                .id(domain.getId())
                .assetId(domain.getAssetId())
                .title(domain.getTitle())
                .frequency(domain.getFrequency())
                .assignedTechnician(domain.getAssignedTechnician())
                .nextDueDate(domain.getNextDueDate())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private MaintenanceSchedule toDomain(MaintenanceScheduleEntity entity) {
        return MaintenanceSchedule.builder()
                .id(entity.getId())
                .assetId(entity.getAssetId())
                .title(entity.getTitle())
                .frequency(entity.getFrequency())
                .assignedTechnician(entity.getAssignedTechnician())
                .nextDueDate(entity.getNextDueDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.EmergencySosAlert;
import com.hostelmind.domain.model.SosAlertStatus;
import com.hostelmind.domain.repository.EmergencySosAlertRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.EmergencySosAlertEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaEmergencySosAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmergencySosAlertRepositoryAdapter implements EmergencySosAlertRepositoryPort {

    private final JpaEmergencySosAlertRepository jpaRepository;

    @Override
    public EmergencySosAlert save(EmergencySosAlert domain) {
        EmergencySosAlertEntity entity = toEntity(domain);
        EmergencySosAlertEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmergencySosAlert> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<EmergencySosAlert> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EmergencySosAlert> findByStatus(SosAlertStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<EmergencySosAlert> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private EmergencySosAlertEntity toEntity(EmergencySosAlert domain) {
        return EmergencySosAlertEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .hostelId(domain.getHostelId())
                .roomNumber(domain.getRoomNumber())
                .sosType(domain.getSosType())
                .locationDetails(domain.getLocationDetails())
                .status(domain.getStatus())
                .triggeredAt(domain.getTriggeredAt())
                .resolvedAt(domain.getResolvedAt())
                .build();
    }

    private EmergencySosAlert toDomain(EmergencySosAlertEntity entity) {
        return EmergencySosAlert.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .hostelId(entity.getHostelId())
                .roomNumber(entity.getRoomNumber())
                .sosType(entity.getSosType())
                .locationDetails(entity.getLocationDetails())
                .status(entity.getStatus())
                .triggeredAt(entity.getTriggeredAt())
                .resolvedAt(entity.getResolvedAt())
                .build();
    }
}

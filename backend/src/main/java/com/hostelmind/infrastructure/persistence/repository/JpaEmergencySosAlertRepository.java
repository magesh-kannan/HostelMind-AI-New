package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.SosAlertStatus;
import com.hostelmind.infrastructure.persistence.entity.EmergencySosAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaEmergencySosAlertRepository extends JpaRepository<EmergencySosAlertEntity, UUID> {
    List<EmergencySosAlertEntity> findByStudentId(UUID studentId);
    List<EmergencySosAlertEntity> findByStatus(SosAlertStatus status);
}

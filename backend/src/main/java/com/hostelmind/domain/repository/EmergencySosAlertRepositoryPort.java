package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.EmergencySosAlert;
import com.hostelmind.domain.model.SosAlertStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmergencySosAlertRepositoryPort {
    EmergencySosAlert save(EmergencySosAlert alert);
    Optional<EmergencySosAlert> findById(UUID id);
    List<EmergencySosAlert> findByStudentId(UUID studentId);
    List<EmergencySosAlert> findByStatus(SosAlertStatus status);
    List<EmergencySosAlert> findAll();
}

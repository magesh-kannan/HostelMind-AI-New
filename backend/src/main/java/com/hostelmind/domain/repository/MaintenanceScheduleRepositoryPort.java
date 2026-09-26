package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.MaintenanceSchedule;
import com.hostelmind.domain.model.MaintenanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceScheduleRepositoryPort {
    MaintenanceSchedule save(MaintenanceSchedule schedule);
    Optional<MaintenanceSchedule> findById(UUID id);
    List<MaintenanceSchedule> findByAssetId(UUID assetId);
    List<MaintenanceSchedule> findByStatus(MaintenanceStatus status);
    List<MaintenanceSchedule> findDueBefore(LocalDate date);
    List<MaintenanceSchedule> findAll();
    void deleteById(UUID id);
}

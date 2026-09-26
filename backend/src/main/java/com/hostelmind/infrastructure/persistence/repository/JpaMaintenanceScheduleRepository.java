package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.MaintenanceStatus;
import com.hostelmind.infrastructure.persistence.entity.MaintenanceScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JpaMaintenanceScheduleRepository extends JpaRepository<MaintenanceScheduleEntity, UUID> {
    List<MaintenanceScheduleEntity> findByAssetId(UUID assetId);
    List<MaintenanceScheduleEntity> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM MaintenanceScheduleEntity m WHERE m.nextDueDate <= :date")
    List<MaintenanceScheduleEntity> findDueBefore(LocalDate date);
}

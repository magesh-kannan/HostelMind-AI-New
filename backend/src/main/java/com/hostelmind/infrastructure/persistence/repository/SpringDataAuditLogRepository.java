package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    List<AuditLogEntity> findByUserId(UUID userId);
    List<AuditLogEntity> findByEntityType(String entityType);
    List<AuditLogEntity> findByActionContainingIgnoreCase(String action);
}

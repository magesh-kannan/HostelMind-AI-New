package com.hostelmind.domain.port;

import com.hostelmind.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findAll();
    List<AuditLog> findByUserId(UUID userId);
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByAction(String action);
}

package com.hostelmind.domain.port;

import com.hostelmind.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepositoryPort {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByUserId(UUID userId);
}

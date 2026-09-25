package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AuditLog;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.AuditLogEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final SpringDataAuditLogRepository springDataAuditLogRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = springDataAuditLogRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditLog> findByUserId(UUID userId) {
        return springDataAuditLogRepository.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AuditLogEntity toEntity(AuditLog log) {
        return AuditLogEntity.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .action(entity.getAction())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .details(entity.getDetails())
                .ipAddress(entity.getIpAddress())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

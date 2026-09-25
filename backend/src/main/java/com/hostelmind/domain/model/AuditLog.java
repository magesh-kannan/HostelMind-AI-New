package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private String ipAddress;
    private Instant createdAt;
}

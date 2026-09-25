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
public class RefreshToken {
    private UUID id;
    private UUID userId;
    private String token;
    private Instant expiryDate;
    private boolean revoked;
    private Instant createdAt;
}

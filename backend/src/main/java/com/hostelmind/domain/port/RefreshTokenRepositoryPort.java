package com.hostelmind.domain.port;

import com.hostelmind.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    void revokeAllByUserId(UUID userId);
}

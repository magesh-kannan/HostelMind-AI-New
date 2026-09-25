package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.RefreshToken;
import com.hostelmind.domain.port.RefreshTokenRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.RefreshTokenEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final SpringDataRefreshTokenRepository springDataRefreshTokenRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        RefreshTokenEntity saved = springDataRefreshTokenRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springDataRefreshTokenRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springDataRefreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        springDataRefreshTokenRepository.deleteByUserId(userId);
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        return RefreshTokenEntity.builder()
                .id(token.getId())
                .userId(token.getUserId())
                .token(token.getToken())
                .expiryDate(token.getExpiryDate())
                .revoked(token.isRevoked())
                .createdAt(token.getCreatedAt())
                .build();
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

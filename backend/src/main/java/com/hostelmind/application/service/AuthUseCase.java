package com.hostelmind.application.service;

import com.hostelmind.application.dto.*;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.exception.ResourceNotFoundException;
import com.hostelmind.domain.exception.UnauthorizedException;
import com.hostelmind.domain.model.AuditLog;
import com.hostelmind.domain.model.RefreshToken;
import com.hostelmind.domain.model.Role;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.port.RefreshTokenRepositoryPort;
import com.hostelmind.domain.port.UserRepositoryPort;
import com.hostelmind.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final AuditLogRepositoryPort auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DomainException("Email is already registered: " + request.getEmail());
        }

        Set<Role> roles = (request.getRoles() != null && !request.getRoles().isEmpty())
                ? request.getRoles()
                : Set.of(Role.ROLE_STUDENT);

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .active(true)
                .roles(roles)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .userId(savedUser.getId())
                .action("USER_REGISTER")
                .entityType("User")
                .entityId(savedUser.getId().toString())
                .details("Registered user with email: " + savedUser.getEmail())
                .createdAt(Instant.now())
                .build());

        return createAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Audit log
        auditLogRepository.save(AuditLog.builder()
                .userId(user.getId())
                .action("USER_LOGIN")
                .entityType("User")
                .entityId(user.getId().toString())
                .details("User logged in successfully")
                .createdAt(Instant.now())
                .build());

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Revoke current token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return createAuthResponse(user);
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .action("USER_LOGOUT")
                .entityType("User")
                .entityId(userId.toString())
                .details("User logged out")
                .createdAt(Instant.now())
                .build());
    }

    public UserDto getCurrentUserDto(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDto(user);
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshTokenStr = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .user(mapToUserDto(user))
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .roles(user.getRoles())
                .build();
    }
}

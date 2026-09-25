package com.hostelmind.application.service;

import com.hostelmind.application.dto.AuthRequest;
import com.hostelmind.application.dto.AuthResponse;
import com.hostelmind.application.dto.RegisterRequest;
import com.hostelmind.domain.exception.UnauthorizedException;
import com.hostelmind.domain.model.RefreshToken;
import com.hostelmind.domain.model.Role;
import com.hostelmind.domain.model.User;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.port.RefreshTokenRepositoryPort;
import com.hostelmind.domain.port.UserRepositoryPort;
import com.hostelmind.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .email("student@hostelmind.ai")
                .passwordHash("hashed_password")
                .fullName("Test Student")
                .active(true)
                .roles(Set.of(Role.ROLE_STUDENT))
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        RegisterRequest request = RegisterRequest.builder()
                .email("student@hostelmind.ai")
                .password("password123")
                .fullName("Test Student")
                .roles(Set.of(Role.ROLE_STUDENT))
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(userRepository.save(any())).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(any())).thenReturn("mock_jwt_token");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authUseCase.register(request);

        assertNotNull(response);
        assertEquals("mock_jwt_token", response.getAccessToken());
        assertEquals("student@hostelmind.ai", response.getUser().getEmail());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowUnauthorized() {
        AuthRequest request = AuthRequest.builder()
                .email("student@hostelmind.ai")
                .password("wrongpassword")
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authUseCase.login(request));
    }
}

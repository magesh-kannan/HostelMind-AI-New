package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.UserDto;
import com.hostelmind.application.service.AuthUseCase;
import com.hostelmind.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthUseCase authUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserDto dto = authUseCase.getCurrentUserDto(principal.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Admin Area - Access Granted");
    }
}

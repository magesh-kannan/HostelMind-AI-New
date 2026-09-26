package com.hostelmind.infrastructure.web.controller;

import com.hostelmind.application.dto.mess.*;
import com.hostelmind.application.service.MessUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mess")
@RequiredArgsConstructor
public class MessController {

    private final MessUseCase messUseCase;

    // ─── Menu Operations ──────────────────────────────────────────────────────

    @PostMapping("/menu")
    public ResponseEntity<MessMenuDto> createOrUpdateMenu(@Valid @RequestBody CreateMessMenuRequest request) {
        MessMenuDto dto = messUseCase.createOrUpdateMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/menu")
    public ResponseEntity<List<MessMenuDto>> getWeeklyMenu(@RequestParam(required = false) UUID hostelId) {
        return ResponseEntity.ok(messUseCase.getWeeklyMenu(hostelId));
    }

    // ─── QR Code & Attendance ──────────────────────────────────────────────────

    @PostMapping("/generate-qr")
    public ResponseEntity<QrTokenResponse> generateQrToken(
            Authentication authentication,
            @Valid @RequestBody GenerateQrTokenRequest request
    ) {
        UUID studentId = extractUserId(authentication);
        QrTokenResponse response = messUseCase.generateQrToken(studentId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scan-qr")
    public ResponseEntity<MealAttendanceDto> scanQrToken(@Valid @RequestBody ScanQrTokenRequest request) {
        MealAttendanceDto attendance = messUseCase.scanQrToken(request);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<MealAttendanceDto>> getAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(messUseCase.getAttendanceForDate(date));
    }

    // ─── Feedback & Stats ──────────────────────────────────────────────────────

    @PostMapping("/feedback")
    public ResponseEntity<MessFeedbackDto> submitFeedback(
            Authentication authentication,
            @Valid @RequestBody CreateMessFeedbackRequest request
    ) {
        UUID studentId = extractUserId(authentication);
        MessFeedbackDto dto = messUseCase.submitFeedback(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<MessFeedbackDto>> getAllFeedback() {
        return ResponseEntity.ok(messUseCase.getAllFeedback());
    }

    @GetMapping("/stats")
    public ResponseEntity<MessStatsDto> getMessStats() {
        return ResponseEntity.ok(messUseCase.getMessStats());
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }
}

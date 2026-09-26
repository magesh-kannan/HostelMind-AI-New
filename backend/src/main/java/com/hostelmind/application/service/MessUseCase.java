package com.hostelmind.application.service;

import com.hostelmind.application.dto.mess.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.MealAttendanceRepositoryPort;
import com.hostelmind.domain.repository.MessFeedbackRepositoryPort;
import com.hostelmind.domain.repository.MessMenuRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessUseCase {

    private final MessMenuRepositoryPort menuRepository;
    private final MealAttendanceRepositoryPort attendanceRepository;
    private final MessFeedbackRepositoryPort feedbackRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    // ─── Mess Menu Operations ──────────────────────────────────────────────────

    @Transactional
    public MessMenuDto createOrUpdateMenu(CreateMessMenuRequest req) {
        Optional<MessMenu> existing = menuRepository.findByHostelDayAndMeal(
                req.getHostelId(), req.getDayOfWeek(), req.getMealType()
        );

        MessMenu menu;
        if (existing.isPresent()) {
            menu = existing.get();
            menu.setItems(req.getItems());
            menu.setCalorieCount(req.getCalorieCount() != null ? req.getCalorieCount() : 0);
            menu.setSpecialNotes(req.getSpecialNotes());
            menu.setUpdatedAt(Instant.now());
        } else {
            menu = MessMenu.builder()
                    .hostelId(req.getHostelId())
                    .dayOfWeek(req.getDayOfWeek())
                    .mealType(req.getMealType())
                    .items(req.getItems())
                    .calorieCount(req.getCalorieCount() != null ? req.getCalorieCount() : 0)
                    .specialNotes(req.getSpecialNotes())
                    .isActive(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
        }

        MessMenu saved = menuRepository.save(menu);

        auditLogRepository.save(AuditLog.builder()
                .action("SAVE_MESS_MENU")
                .entityType("MessMenu")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("Updated menu for %s %s", req.getDayOfWeek(), req.getMealType()))
                .createdAt(Instant.now())
                .build());

        return mapToMenuDto(saved);
    }

    public List<MessMenuDto> getWeeklyMenu(UUID hostelId) {
        List<MessMenu> list = hostelId != null
                ? menuRepository.findByHostelId(hostelId)
                : menuRepository.findAll();
        return list.stream().map(this::mapToMenuDto).collect(Collectors.toList());
    }

    // ─── QR Code & Attendance ──────────────────────────────────────────────────

    public QrTokenResponse generateQrToken(UUID studentId, GenerateQrTokenRequest req) {
        LocalDate date = req.getDate() != null ? req.getDate() : LocalDate.now();
        String payload = String.format("MESS_PASS|%s|%s|%s", studentId, req.getMealType(), date);
        String token = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        return QrTokenResponse.builder()
                .qrToken(token)
                .studentId(studentId)
                .mealType(req.getMealType())
                .date(date)
                .expiresAt(Instant.now().plusSeconds(14400)) // 4 hour validity
                .build();
    }

    @Transactional
    public MealAttendanceDto scanQrToken(ScanQrTokenRequest req) {
        String token = req.getQrToken();
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid QR code token format");
        }

        String[] parts = decoded.split("\\|");
        if (parts.length < 4 || !"MESS_PASS".equals(parts[0])) {
            throw new IllegalArgumentException("Unrecognized Mess Pass QR structure");
        }

        UUID studentId = UUID.fromString(parts[1]);
        MealType mealType = MealType.valueOf(parts[2]);
        LocalDate date = LocalDate.parse(parts[3]);

        // Duplicate scan check
        Optional<MealAttendance> existing = attendanceRepository.findByStudentMealAndDate(studentId, mealType, date);
        if (existing.isPresent()) {
            throw new IllegalStateException(String.format("Student has already been served %s on %s", mealType, date));
        }

        MealAttendance attendance = MealAttendance.builder()
                .studentId(studentId)
                .mealType(mealType)
                .attendanceDate(date)
                .qrToken(token)
                .status(MealAttendanceStatus.SERVED)
                .scannedAt(Instant.now())
                .build();

        MealAttendance saved = attendanceRepository.save(attendance);

        auditLogRepository.save(AuditLog.builder()
                .userId(studentId)
                .action("SCAN_MEAL_ATTENDANCE")
                .entityType("MealAttendance")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("Verified meal attendance for %s on %s", mealType, date))
                .createdAt(Instant.now())
                .build());

        return mapToAttendanceDto(saved);
    }

    public List<MealAttendanceDto> getAttendanceForDate(LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return attendanceRepository.findByDate(target).stream()
                .map(this::mapToAttendanceDto)
                .collect(Collectors.toList());
    }

    // ─── Mess Feedback ────────────────────────────────────────────────────────

    @Transactional
    public MessFeedbackDto submitFeedback(UUID studentId, CreateMessFeedbackRequest req) {
        MessFeedback feedback = MessFeedback.builder()
                .studentId(studentId)
                .menuId(req.getMenuId())
                .rating(req.getRating())
                .comment(req.getComment())
                .createdAt(Instant.now())
                .build();

        MessFeedback saved = feedbackRepository.save(feedback);
        return mapToFeedbackDto(saved);
    }

    public List<MessFeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(this::mapToFeedbackDto)
                .collect(Collectors.toList());
    }

    // ─── Mess Stats ───────────────────────────────────────────────────────────

    public MessStatsDto getMessStats() {
        LocalDate today = LocalDate.now();
        List<MealAttendance> todayList = attendanceRepository.findByDate(today);
        List<MessFeedback> allFeedback = feedbackRepository.findAll();

        long b = todayList.stream().filter(a -> a.getMealType() == MealType.BREAKFAST).count();
        long l = todayList.stream().filter(a -> a.getMealType() == MealType.LUNCH).count();
        long s = todayList.stream().filter(a -> a.getMealType() == MealType.SNACKS).count();
        long d = todayList.stream().filter(a -> a.getMealType() == MealType.DINNER).count();

        double avgRating = allFeedback.stream()
                .mapToInt(MessFeedback::getRating)
                .average()
                .orElse(0.0);

        return MessStatsDto.builder()
                .totalServedToday(todayList.size())
                .breakfastServedToday(b)
                .lunchServedToday(l)
                .snacksServedToday(s)
                .dinnerServedToday(d)
                .averageRating(Math.round(avgRating * 10.0) / 10.0)
                .build();
    }

    // ─── Mapping Helpers ───────────────────────────────────────────────────────

    private MessMenuDto mapToMenuDto(MessMenu m) {
        return MessMenuDto.builder()
                .id(m.getId())
                .hostelId(m.getHostelId())
                .dayOfWeek(m.getDayOfWeek())
                .mealType(m.getMealType())
                .items(m.getItems())
                .calorieCount(m.getCalorieCount())
                .specialNotes(m.getSpecialNotes())
                .isActive(m.getIsActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private MealAttendanceDto mapToAttendanceDto(MealAttendance a) {
        return MealAttendanceDto.builder()
                .id(a.getId())
                .studentId(a.getStudentId())
                .hostelId(a.getHostelId())
                .mealType(a.getMealType())
                .attendanceDate(a.getAttendanceDate())
                .qrToken(a.getQrToken())
                .status(a.getStatus())
                .scannedAt(a.getScannedAt())
                .build();
    }

    private MessFeedbackDto mapToFeedbackDto(MessFeedback f) {
        return MessFeedbackDto.builder()
                .id(f.getId())
                .studentId(f.getStudentId())
                .menuId(f.getMenuId())
                .rating(f.getRating())
                .comment(f.getComment())
                .createdAt(f.getCreatedAt())
                .build();
    }
}

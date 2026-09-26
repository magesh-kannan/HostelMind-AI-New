package com.hostelmind.application.service;

import com.hostelmind.application.dto.mess.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.MealAttendanceRepositoryPort;
import com.hostelmind.domain.repository.MessFeedbackRepositoryPort;
import com.hostelmind.domain.repository.MessMenuRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessUseCaseTest {

    @Mock
    private MessMenuRepositoryPort menuRepository;

    @Mock
    private MealAttendanceRepositoryPort attendanceRepository;

    @Mock
    private MessFeedbackRepositoryPort feedbackRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private MessUseCase messUseCase;

    private UUID studentId;
    private UUID hostelId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        hostelId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create or update mess menu successfully")
    void createOrUpdateMenu_Success() {
        CreateMessMenuRequest req = new CreateMessMenuRequest(
                hostelId, DayOfWeek.MONDAY, MealType.LUNCH,
                "Paneer Butter Masala, Roti, Rice, Dal", 650, "Vegetarian"
        );

        when(menuRepository.findByHostelDayAndMeal(hostelId, DayOfWeek.MONDAY, MealType.LUNCH))
                .thenReturn(Optional.empty());
        when(menuRepository.save(any())).thenAnswer(inv -> {
            MessMenu m = inv.getArgument(0);
            if (m.getId() == null) m.setId(UUID.randomUUID());
            return m;
        });

        var dto = messUseCase.createOrUpdateMenu(req);

        assertNotNull(dto);
        assertEquals(DayOfWeek.MONDAY, dto.getDayOfWeek());
        assertEquals(MealType.LUNCH, dto.getMealType());
        assertEquals(650, dto.getCalorieCount());

        verify(menuRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should generate valid QR pass token for meal")
    void generateQrToken_Success() {
        GenerateQrTokenRequest req = new GenerateQrTokenRequest(MealType.DINNER, LocalDate.now());

        var tokenRes = messUseCase.generateQrToken(studentId, req);

        assertNotNull(tokenRes);
        assertNotNull(tokenRes.getQrToken());
        assertEquals(studentId, tokenRes.getStudentId());
        assertEquals(MealType.DINNER, tokenRes.getMealType());
    }

    @Test
    @DisplayName("Should scan valid QR token and record meal attendance")
    void scanQrToken_Success() {
        LocalDate today = LocalDate.now();
        var tokenRes = messUseCase.generateQrToken(studentId, new GenerateQrTokenRequest(MealType.BREAKFAST, today));

        when(attendanceRepository.findByStudentMealAndDate(studentId, MealType.BREAKFAST, today))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(inv -> {
            MealAttendance a = inv.getArgument(0);
            if (a.getId() == null) a.setId(UUID.randomUUID());
            return a;
        });

        ScanQrTokenRequest req = new ScanQrTokenRequest(tokenRes.getQrToken());

        var dto = messUseCase.scanQrToken(req);

        assertNotNull(dto);
        assertEquals(studentId, dto.getStudentId());
        assertEquals(MealType.BREAKFAST, dto.getMealType());
        assertEquals(MealAttendanceStatus.SERVED, dto.getStatus());

        verify(attendanceRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when duplicate QR scan is attempted")
    void scanQrToken_DuplicateScan_ThrowsException() {
        LocalDate today = LocalDate.now();
        var tokenRes = messUseCase.generateQrToken(studentId, new GenerateQrTokenRequest(MealType.LUNCH, today));

        MealAttendance existingAttendance = MealAttendance.builder()
                .id(UUID.randomUUID())
                .studentId(studentId)
                .mealType(MealType.LUNCH)
                .attendanceDate(today)
                .status(MealAttendanceStatus.SERVED)
                .build();

        when(attendanceRepository.findByStudentMealAndDate(studentId, MealType.LUNCH, today))
                .thenReturn(Optional.of(existingAttendance));

        ScanQrTokenRequest req = new ScanQrTokenRequest(tokenRes.getQrToken());

        assertThrows(IllegalStateException.class, () -> messUseCase.scanQrToken(req));
    }

    @Test
    @DisplayName("Should calculate average feedback rating and turnout in mess stats")
    void getMessStats_Success() {
        LocalDate today = LocalDate.now();

        List<MealAttendance> attendanceList = List.of(
                MealAttendance.builder().mealType(MealType.BREAKFAST).attendanceDate(today).build(),
                MealAttendance.builder().mealType(MealType.LUNCH).attendanceDate(today).build(),
                MealAttendance.builder().mealType(MealType.LUNCH).attendanceDate(today).build()
        );

        List<MessFeedback> feedbackList = List.of(
                MessFeedback.builder().rating(4).build(),
                MessFeedback.builder().rating(5).build()
        );

        when(attendanceRepository.findByDate(today)).thenReturn(attendanceList);
        when(feedbackRepository.findAll()).thenReturn(feedbackList);

        var stats = messUseCase.getMessStats();

        assertNotNull(stats);
        assertEquals(3, stats.getTotalServedToday());
        assertEquals(1, stats.getBreakfastServedToday());
        assertEquals(2, stats.getLunchServedToday());
        assertEquals(4.5, stats.getAverageRating());
    }
}

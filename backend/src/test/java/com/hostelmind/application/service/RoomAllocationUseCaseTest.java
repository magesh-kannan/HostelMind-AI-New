package com.hostelmind.application.service;

import com.hostelmind.application.dto.AllocateBedRequest;
import com.hostelmind.application.dto.RoomAllocationDto;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAllocationUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private BedRepositoryPort bedRepository;

    @Mock
    private RoomRepositoryPort roomRepository;

    @Mock
    private HostelRepositoryPort hostelRepository;

    @Mock
    private RoomAllocationRepositoryPort allocationRepository;

    @Mock
    private AllocationHistoryRepositoryPort historyRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private RoomAllocationUseCase allocationUseCase;

    private User sampleStudent;
    private Bed vacantBed;
    private Room availableRoom;
    private UUID wardenId;

    @BeforeEach
    void setUp() {
        wardenId = UUID.randomUUID();
        sampleStudent = User.builder()
                .id(UUID.randomUUID())
                .email("student1@hostelmind.ai")
                .fullName("John Doe")
                .active(true)
                .build();

        availableRoom = Room.builder()
                .id(UUID.randomUUID())
                .roomNumber("101")
                .roomType(RoomType.DOUBLE)
                .capacity(2)
                .occupiedCount(0)
                .status(RoomStatus.AVAILABLE)
                .monthlyRent(new BigDecimal("5000.00"))
                .build();

        vacantBed = Bed.builder()
                .id(UUID.randomUUID())
                .roomId(availableRoom.getId())
                .bedNumber("Bed A")
                .status(BedStatus.VACANT)
                .build();
    }

    @Test
    void allocateBed_Success() {
        AllocateBedRequest request = AllocateBedRequest.builder()
                .studentId(sampleStudent.getId())
                .bedId(vacantBed.getId())
                .academicYear("2026-2027")
                .startDate(LocalDate.now())
                .build();

        when(userRepository.findById(sampleStudent.getId())).thenReturn(Optional.of(sampleStudent));
        when(allocationRepository.findActiveByStudentId(sampleStudent.getId())).thenReturn(Optional.empty());
        when(bedRepository.findById(vacantBed.getId())).thenReturn(Optional.of(vacantBed));
        when(roomRepository.findById(availableRoom.getId())).thenReturn(Optional.of(availableRoom));
        when(allocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RoomAllocationDto dto = allocationUseCase.allocateBed(request, wardenId);

        assertNotNull(dto);
        assertEquals(AllocationStatus.ACTIVE, dto.getStatus());
        assertEquals("101", dto.getRoomNumber());
        assertEquals("Bed A", dto.getBedNumber());

        verify(bedRepository, times(1)).save(argThat(b -> b.getStatus() == BedStatus.OCCUPIED));
        verify(roomRepository, times(1)).save(argThat(r -> r.getOccupiedCount() == 1));
        verify(historyRepository, times(1)).save(any());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void allocateBed_DuplicateActiveAllocation_ThrowsDomainException() {
        AllocateBedRequest request = AllocateBedRequest.builder()
                .studentId(sampleStudent.getId())
                .bedId(vacantBed.getId())
                .build();

        when(userRepository.findById(sampleStudent.getId())).thenReturn(Optional.of(sampleStudent));
        when(allocationRepository.findActiveByStudentId(sampleStudent.getId()))
                .thenReturn(Optional.of(RoomAllocation.builder().status(AllocationStatus.ACTIVE).build()));

        assertThrows(DomainException.class, () -> allocationUseCase.allocateBed(request, wardenId));
    }

    @Test
    void allocateBed_OccupiedBed_ThrowsDomainException() {
        vacantBed.setStatus(BedStatus.OCCUPIED);
        AllocateBedRequest request = AllocateBedRequest.builder()
                .studentId(sampleStudent.getId())
                .bedId(vacantBed.getId())
                .build();

        when(userRepository.findById(sampleStudent.getId())).thenReturn(Optional.of(sampleStudent));
        when(allocationRepository.findActiveByStudentId(sampleStudent.getId())).thenReturn(Optional.empty());
        when(bedRepository.findById(vacantBed.getId())).thenReturn(Optional.of(vacantBed));

        assertThrows(DomainException.class, () -> allocationUseCase.allocateBed(request, wardenId));
    }
}

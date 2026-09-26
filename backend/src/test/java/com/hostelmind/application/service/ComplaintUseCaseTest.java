package com.hostelmind.application.service;

import com.hostelmind.application.dto.CreateComplaintRequest;
import com.hostelmind.application.dto.UpdateComplaintStatusRequest;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintUseCaseTest {

    @Mock private ComplaintRepositoryPort complaintRepository;
    @Mock private ComplaintStatusHistoryRepositoryPort historyRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private ComplaintUseCase complaintUseCase;

    private UUID studentId;
    private UUID wardenId;
    private UUID hostelId;
    private User sampleStudent;
    private Complaint newComplaint;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        wardenId  = UUID.randomUUID();
        hostelId  = UUID.randomUUID();

        sampleStudent = User.builder()
                .id(studentId)
                .email("student@test.com")
                .fullName("Test Student")
                .active(true)
                .build();

        newComplaint = Complaint.builder()
                .id(UUID.randomUUID())
                .studentId(studentId)
                .hostelId(hostelId)
                .title("Leaking tap")
                .description("The bathroom tap is leaking continuously")
                .category(ComplaintCategory.PLUMBING)
                .priority(ComplaintPriority.MEDIUM)
                .status(ComplaintStatus.NEW)
                .reopenCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ────────────────────────────────────────────────────────────
    // CREATE
    // ────────────────────────────────────────────────────────────

    @Test
    void createComplaint_Success() {
        CreateComplaintRequest req = new CreateComplaintRequest();
        req.setHostelId(hostelId);
        req.setTitle("Leaking tap");
        req.setDescription("Bathroom tap is leaking");
        req.setCategory(ComplaintCategory.PLUMBING);

        when(userRepository.findById(studentId)).thenReturn(Optional.of(sampleStudent));
        // save() must return a complaint with a real UUID (JPA @UuidGenerator only fires in DB context)
        when(complaintRepository.save(any())).thenAnswer(inv -> {
            Complaint c = inv.getArgument(0);
            if (c.getId() == null) c.setId(UUID.randomUUID());
            return c;
        });
        when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var dto = complaintUseCase.createComplaint(studentId, req);

        assertNotNull(dto);
        assertEquals(ComplaintStatus.NEW, dto.getStatus());
        assertEquals(ComplaintPriority.MEDIUM, dto.getPriority()); // default priority
        assertEquals("Leaking tap", dto.getTitle());

        verify(complaintRepository).save(any());
        verify(historyRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void createComplaint_StudentNotFound_ThrowsException() {
        CreateComplaintRequest req = new CreateComplaintRequest();
        req.setHostelId(hostelId);
        req.setTitle("Leaking tap");
        req.setDescription("Bathroom tap is leaking");
        req.setCategory(ComplaintCategory.PLUMBING);

        when(userRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> complaintUseCase.createComplaint(studentId, req));
    }

    // ────────────────────────────────────────────────────────────
    // STATE MACHINE TRANSITIONS — Happy paths
    // ────────────────────────────────────────────────────────────

    @Nested
    class StateTransitions {

        @Test
        void transition_NewToClassified_Success() {
            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(any())).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.CLASSIFIED);
            req.setNote("Auto classified by AI");

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req);

            assertEquals(ComplaintStatus.CLASSIFIED, dto.getStatus());
            verify(historyRepository).save(argThat(h ->
                    h.getFromStatus() == ComplaintStatus.NEW
                    && h.getToStatus() == ComplaintStatus.CLASSIFIED
            ));
        }

        @Test
        void transition_ClassifiedToPrioritized_SetsSlaDeadline() {
            newComplaint.setStatus(ComplaintStatus.CLASSIFIED);
            Instant sla = Instant.now().plusSeconds(86400); // 24h SLA

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(any())).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.PRIORITIZED);
            req.setPriority(ComplaintPriority.HIGH);
            req.setSlaDeadline(sla);

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req);

            assertEquals(ComplaintStatus.PRIORITIZED, dto.getStatus());
            assertEquals(ComplaintPriority.HIGH, dto.getPriority());
            assertEquals(sla, dto.getSlaDeadline());
        }

        @Test
        void transition_PrioritizedToAssigned_RequiresAssigneeId() {
            newComplaint.setStatus(ComplaintStatus.PRIORITIZED);
            UUID assigneeId = UUID.randomUUID();
            User assignee = User.builder().id(assigneeId).fullName("Warden A").build();

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
            when(userRepository.findById(studentId)).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.ASSIGNED);
            req.setAssignedToId(assigneeId);

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req);

            assertEquals(ComplaintStatus.ASSIGNED, dto.getStatus());
            assertEquals(assigneeId, dto.getAssignedToId());
        }

        @Test
        void transition_PrioritizedToAssigned_MissingAssigneeId_ThrowsDomainException() {
            newComplaint.setStatus(ComplaintStatus.PRIORITIZED);

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.ASSIGNED);
            // assignedToId intentionally omitted

            assertThrows(DomainException.class,
                    () -> complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req));
        }

        @Test
        void transition_InProgressToResolved_SetsResolvedAt() {
            newComplaint.setStatus(ComplaintStatus.IN_PROGRESS);

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(any())).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.RESOLVED);

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req);

            assertEquals(ComplaintStatus.RESOLVED, dto.getStatus());
            assertNotNull(dto.getResolvedAt());
        }

        @Test
        void transition_VerifiedToClosed_SetsClosedAt() {
            newComplaint.setStatus(ComplaintStatus.VERIFIED);

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(any())).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.CLOSED);

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req);

            assertEquals(ComplaintStatus.CLOSED, dto.getStatus());
            assertNotNull(dto.getClosedAt());
        }

        @Test
        void transition_ClosedToReopened_IncrementsReopenCount() {
            newComplaint.setStatus(ComplaintStatus.CLOSED);
            newComplaint.setReopenCount(0);

            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));
            when(complaintRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(historyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.findById(any())).thenReturn(Optional.of(sampleStudent));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.REOPENED);
            req.setNote("Issue persists");

            var dto = complaintUseCase.transitionStatus(newComplaint.getId(), studentId, req);

            assertEquals(ComplaintStatus.REOPENED, dto.getStatus());
            assertEquals(1, dto.getReopenCount());
            assertNull(dto.getResolvedAt());
            assertNull(dto.getClosedAt());
        }
    }

    // ────────────────────────────────────────────────────────────
    // STATE MACHINE — Invalid transitions
    // ────────────────────────────────────────────────────────────

    @Nested
    class InvalidTransitions {

        @Test
        void transition_NewToResolved_InvalidThrowsDomainException() {
            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.RESOLVED);

            assertThrows(DomainException.class,
                    () -> complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req));
        }

        @Test
        void transition_NewToClosed_InvalidThrowsDomainException() {
            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.CLOSED);

            assertThrows(DomainException.class,
                    () -> complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req));
        }

        @Test
        void transition_NewToInProgress_InvalidThrowsDomainException() {
            when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

            var req = new UpdateComplaintStatusRequest();
            req.setStatus(ComplaintStatus.IN_PROGRESS);

            assertThrows(DomainException.class,
                    () -> complaintUseCase.transitionStatus(newComplaint.getId(), wardenId, req));
        }
    }

    // ────────────────────────────────────────────────────────────
    // REOPENING GUARD
    // ────────────────────────────────────────────────────────────

    @Test
    void reopen_ExceedMaxReopenCount_ThrowsDomainException() {
        newComplaint.setStatus(ComplaintStatus.CLOSED);
        newComplaint.setReopenCount(3); // at max already

        when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

        var req = new UpdateComplaintStatusRequest();
        req.setStatus(ComplaintStatus.REOPENED);

        DomainException ex = assertThrows(DomainException.class,
                () -> complaintUseCase.transitionStatus(newComplaint.getId(), studentId, req));

        assertTrue(ex.getMessage().contains("Cannot reopen complaint more than"));
    }

    // ────────────────────────────────────────────────────────────
    // DELETE GUARD
    // ────────────────────────────────────────────────────────────

    @Test
    void deleteComplaint_NewStatus_Success() {
        when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

        complaintUseCase.deleteComplaint(newComplaint.getId(), wardenId);

        verify(complaintRepository).deleteById(newComplaint.getId());
    }

    @Test
    void deleteComplaint_InProgressStatus_ThrowsDomainException() {
        newComplaint.setStatus(ComplaintStatus.IN_PROGRESS);
        when(complaintRepository.findById(newComplaint.getId())).thenReturn(Optional.of(newComplaint));

        assertThrows(DomainException.class,
                () -> complaintUseCase.deleteComplaint(newComplaint.getId(), wardenId));
    }

    // ────────────────────────────────────────────────────────────
    // DOMAIN MODEL: canTransitionTo tests
    // ────────────────────────────────────────────────────────────

    @Nested
    class ComplaintDomainModelTests {

        @Test
        void canTransitionTo_AllValidPaths() {
            // NEW -> CLASSIFIED
            Complaint c = Complaint.builder().status(ComplaintStatus.NEW).build();
            assertTrue(c.canTransitionTo(ComplaintStatus.CLASSIFIED));
            assertFalse(c.canTransitionTo(ComplaintStatus.RESOLVED));
            assertFalse(c.canTransitionTo(ComplaintStatus.CLOSED));

            // RESOLVED -> VERIFIED or REOPENED
            c.setStatus(ComplaintStatus.RESOLVED);
            assertTrue(c.canTransitionTo(ComplaintStatus.VERIFIED));
            assertTrue(c.canTransitionTo(ComplaintStatus.REOPENED));
            assertFalse(c.canTransitionTo(ComplaintStatus.NEW));
            assertFalse(c.canTransitionTo(ComplaintStatus.CLOSED));

            // CLOSED -> REOPENED only
            c.setStatus(ComplaintStatus.CLOSED);
            assertTrue(c.canTransitionTo(ComplaintStatus.REOPENED));
            assertFalse(c.canTransitionTo(ComplaintStatus.RESOLVED));
        }

        @Test
        void canTransitionTo_WaitingForStudent_CanGoBackToInProgress() {
            Complaint c = Complaint.builder().status(ComplaintStatus.WAITING_FOR_STUDENT).build();
            assertTrue(c.canTransitionTo(ComplaintStatus.IN_PROGRESS));
            assertTrue(c.canTransitionTo(ComplaintStatus.RESOLVED));
            assertFalse(c.canTransitionTo(ComplaintStatus.CLOSED));
        }
    }
}

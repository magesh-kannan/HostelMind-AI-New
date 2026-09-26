package com.hostelmind.application.service;

import com.hostelmind.application.dto.*;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.exception.ResourceNotFoundException;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Complaint management use case.
 * Enforces the full state machine and all business rules.
 */
@Service
@RequiredArgsConstructor
public class ComplaintUseCase {

    private final ComplaintRepositoryPort complaintRepository;
    private final ComplaintStatusHistoryRepositoryPort historyRepository;
    private final UserRepositoryPort userRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    // ── Reopening guard ──────────────────────────────────────────
    private static final int MAX_REOPEN_COUNT = 3;

    // ── Create ──────────────────────────────────────────────────

    @Transactional
    public ComplaintDto createComplaint(UUID studentId, CreateComplaintRequest req) {
        // Verify student exists
        userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        Complaint complaint = Complaint.builder()
                .studentId(studentId)
                .hostelId(req.getHostelId())
                .roomId(req.getRoomId())
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .priority(req.getPriority() != null ? req.getPriority() : ComplaintPriority.MEDIUM)
                .status(ComplaintStatus.NEW)
                .attachmentUrl(req.getAttachmentUrl())
                .reopenCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Record initial status history
        recordHistory(saved.getId(), null, ComplaintStatus.NEW, studentId, "Complaint submitted");

        // Audit log
        auditLog(studentId, "COMPLAINT_CREATED", "Complaint", saved.getId().toString(),
                "Created complaint: " + saved.getTitle());

        return mapToDto(saved);
    }

    // ── State Transition ────────────────────────────────────────

    @Transactional
    public ComplaintDto transitionStatus(UUID complaintId, UUID actorId,
                                         UpdateComplaintStatusRequest req) {
        Complaint complaint = findOrThrow(complaintId);
        ComplaintStatus targetStatus = req.getStatus();

        // Reopening guard
        if (targetStatus == ComplaintStatus.REOPENED) {
            if (complaint.getReopenCount() >= MAX_REOPEN_COUNT) {
                throw new DomainException("Cannot reopen complaint more than "
                        + MAX_REOPEN_COUNT + " times. Contact administration.");
            }
        }

        // State machine validation
        if (!complaint.canTransitionTo(targetStatus)) {
            throw new DomainException("Invalid status transition: "
                    + complaint.getStatus() + " → " + targetStatus);
        }

        ComplaintStatus previousStatus = complaint.getStatus();
        complaint.setStatus(targetStatus);
        complaint.setUpdatedAt(Instant.now());

        // Apply transition-specific side effects
        switch (targetStatus) {
            case PRIORITIZED -> {
                if (req.getPriority() != null) complaint.setPriority(req.getPriority());
                if (req.getSlaDeadline() != null) complaint.setSlaDeadline(req.getSlaDeadline());
            }
            case ASSIGNED -> {
                if (req.getAssignedToId() == null) {
                    throw new DomainException("assignedToId is required when transitioning to ASSIGNED");
                }
                // Verify assignee exists
                userRepository.findById(req.getAssignedToId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Assignee user not found: " + req.getAssignedToId()));
                complaint.setAssignedToId(req.getAssignedToId());
            }
            case RESOLVED -> complaint.setResolvedAt(Instant.now());
            case CLOSED   -> complaint.setClosedAt(Instant.now());
            case REOPENED -> {
                complaint.setReopenCount(complaint.getReopenCount() + 1);
                complaint.setResolvedAt(null);
                complaint.setClosedAt(null);
            }
        }

        Complaint updated = complaintRepository.save(complaint);

        // Record history
        recordHistory(complaintId, previousStatus, targetStatus, actorId, req.getNote());

        // Audit log
        auditLog(actorId, "COMPLAINT_STATUS_CHANGED", "Complaint", complaintId.toString(),
                previousStatus + " → " + targetStatus);

        return mapToDto(updated);
    }

    // ── Queries ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ComplaintDto getComplaintById(UUID id) {
        return mapToDto(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaintsByStudent(UUID studentId) {
        return complaintRepository.findByStudentId(studentId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaintsByHostel(UUID hostelId) {
        return complaintRepository.findByHostelId(hostelId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintDto> getComplaintsAssignedTo(UUID userId) {
        return complaintRepository.findByAssignedToId(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintStatusHistoryDto> getStatusHistory(UUID complaintId) {
        // Verify complaint exists
        findOrThrow(complaintId);
        return historyRepository.findByComplaintId(complaintId)
                .stream().map(this::mapHistoryToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComplaintStatsDto getStats() {
        long total = complaintRepository.countByStatus(ComplaintStatus.NEW)
                + complaintRepository.countByStatus(ComplaintStatus.CLASSIFIED)
                + complaintRepository.countByStatus(ComplaintStatus.PRIORITIZED)
                + complaintRepository.countByStatus(ComplaintStatus.ASSIGNED)
                + complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)
                + complaintRepository.countByStatus(ComplaintStatus.WAITING_FOR_STUDENT)
                + complaintRepository.countByStatus(ComplaintStatus.RESOLVED)
                + complaintRepository.countByStatus(ComplaintStatus.VERIFIED)
                + complaintRepository.countByStatus(ComplaintStatus.CLOSED)
                + complaintRepository.countByStatus(ComplaintStatus.REOPENED);

        return ComplaintStatsDto.builder()
                .totalComplaints(total)
                .newComplaints(complaintRepository.countByStatus(ComplaintStatus.NEW))
                .inProgressComplaints(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS))
                .resolvedComplaints(complaintRepository.countByStatus(ComplaintStatus.RESOLVED))
                .closedComplaints(complaintRepository.countByStatus(ComplaintStatus.CLOSED))
                .overdueComplaints(0L) // TODO: query overdue from DB with sla_deadline < now() and status != RESOLVED/CLOSED
                .build();
    }

    // ── Delete ──────────────────────────────────────────────────

    @Transactional
    public void deleteComplaint(UUID complaintId, UUID actorId) {
        Complaint complaint = findOrThrow(complaintId);
        // Only NEW/CLASSIFIED complaints can be deleted
        if (complaint.getStatus() != ComplaintStatus.NEW
                && complaint.getStatus() != ComplaintStatus.CLASSIFIED) {
            throw new DomainException("Cannot delete a complaint in status: " + complaint.getStatus());
        }
        complaintRepository.deleteById(complaintId);
        auditLog(actorId, "COMPLAINT_DELETED", "Complaint", complaintId.toString(),
                "Deleted complaint: " + complaint.getTitle());
    }

    // ── Private helpers ─────────────────────────────────────────

    private Complaint findOrThrow(UUID id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));
    }

    private void recordHistory(UUID complaintId, ComplaintStatus from, ComplaintStatus to,
                                UUID changedBy, String note) {
        historyRepository.save(ComplaintStatusHistory.builder()
                .complaintId(complaintId)
                .changedByUserId(changedBy)
                .fromStatus(from)
                .toStatus(to)
                .note(note)
                .changedAt(Instant.now())
                .build());
    }

    private void auditLog(UUID userId, String action, String entityType, String entityId, String details) {
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .createdAt(Instant.now())
                .build());
    }

    private ComplaintDto mapToDto(Complaint c) {
        String studentName = userRepository.findById(c.getStudentId())
                .map(User::getFullName).orElse("Unknown");
        String assignedToName = c.getAssignedToId() != null
                ? userRepository.findById(c.getAssignedToId()).map(User::getFullName).orElse(null)
                : null;

        return ComplaintDto.builder()
                .id(c.getId())
                .studentId(c.getStudentId())
                .studentName(studentName)
                .hostelId(c.getHostelId())
                .roomId(c.getRoomId())
                .assignedToId(c.getAssignedToId())
                .assignedToName(assignedToName)
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .priority(c.getPriority())
                .status(c.getStatus())
                .attachmentUrl(c.getAttachmentUrl())
                .slaDeadline(c.getSlaDeadline())
                .reopenCount(c.getReopenCount())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .resolvedAt(c.getResolvedAt())
                .closedAt(c.getClosedAt())
                .build();
    }

    private ComplaintStatusHistoryDto mapHistoryToDto(ComplaintStatusHistory h) {
        String changedByName = h.getChangedByUserId() != null
                ? userRepository.findById(h.getChangedByUserId()).map(User::getFullName).orElse(null)
                : null;

        return ComplaintStatusHistoryDto.builder()
                .id(h.getId())
                .complaintId(h.getComplaintId())
                .changedByUserId(h.getChangedByUserId())
                .changedByUserName(changedByName)
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .note(h.getNote())
                .changedAt(h.getChangedAt())
                .build();
    }
}

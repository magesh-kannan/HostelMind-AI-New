package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Complaint;
import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintStatus;
import com.hostelmind.domain.port.ComplaintRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.ComplaintEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ComplaintRepositoryAdapter implements ComplaintRepositoryPort {

    private final SpringDataComplaintRepository repository;

    @Override
    public Complaint save(Complaint complaint) {
        return toDomain(repository.save(toEntity(complaint)));
    }

    @Override
    public Optional<Complaint> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Complaint> findByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByHostelId(UUID hostelId) {
        return repository.findByHostelId(hostelId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByAssignedToId(UUID assignedToId) {
        return repository.findByAssignedToId(assignedToId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByStudentIdAndStatus(UUID studentId, ComplaintStatus status) {
        return repository.findByStudentIdAndStatus(studentId, status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Complaint> findByCategory(ComplaintCategory category) {
        return repository.findByCategory(category).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByStatus(ComplaintStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public long countByHostelIdAndStatus(UUID hostelId, ComplaintStatus status) {
        return repository.countByHostelIdAndStatus(hostelId, status);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    // ── Mapping ─────────────────────────────────────────────────

    private ComplaintEntity toEntity(Complaint c) {
        return ComplaintEntity.builder()
                .id(c.getId())
                .studentId(c.getStudentId())
                .hostelId(c.getHostelId())
                .roomId(c.getRoomId())
                .assignedToId(c.getAssignedToId())
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

    private Complaint toDomain(ComplaintEntity e) {
        return Complaint.builder()
                .id(e.getId())
                .studentId(e.getStudentId())
                .hostelId(e.getHostelId())
                .roomId(e.getRoomId())
                .assignedToId(e.getAssignedToId())
                .title(e.getTitle())
                .description(e.getDescription())
                .category(e.getCategory())
                .priority(e.getPriority())
                .status(e.getStatus())
                .attachmentUrl(e.getAttachmentUrl())
                .slaDeadline(e.getSlaDeadline())
                .reopenCount(e.getReopenCount())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .resolvedAt(e.getResolvedAt())
                .closedAt(e.getClosedAt())
                .build();
    }
}

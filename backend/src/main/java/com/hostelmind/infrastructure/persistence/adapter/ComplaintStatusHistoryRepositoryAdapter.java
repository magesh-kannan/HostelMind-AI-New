package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.ComplaintStatusHistory;
import com.hostelmind.domain.port.ComplaintStatusHistoryRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.ComplaintStatusHistoryEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataComplaintStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ComplaintStatusHistoryRepositoryAdapter implements ComplaintStatusHistoryRepositoryPort {

    private final SpringDataComplaintStatusHistoryRepository repository;

    @Override
    public ComplaintStatusHistory save(ComplaintStatusHistory history) {
        return toDomain(repository.save(toEntity(history)));
    }

    @Override
    public List<ComplaintStatusHistory> findByComplaintId(UUID complaintId) {
        return repository.findByComplaintIdOrderByChangedAtAsc(complaintId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private ComplaintStatusHistoryEntity toEntity(ComplaintStatusHistory h) {
        return ComplaintStatusHistoryEntity.builder()
                .id(h.getId())
                .complaintId(h.getComplaintId())
                .changedByUserId(h.getChangedByUserId())
                .fromStatus(h.getFromStatus())
                .toStatus(h.getToStatus())
                .note(h.getNote())
                .changedAt(h.getChangedAt())
                .build();
    }

    private ComplaintStatusHistory toDomain(ComplaintStatusHistoryEntity e) {
        return ComplaintStatusHistory.builder()
                .id(e.getId())
                .complaintId(e.getComplaintId())
                .changedByUserId(e.getChangedByUserId())
                .fromStatus(e.getFromStatus())
                .toStatus(e.getToStatus())
                .note(e.getNote())
                .changedAt(e.getChangedAt())
                .build();
    }
}

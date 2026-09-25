package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AllocationHistory;
import com.hostelmind.domain.port.AllocationHistoryRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.AllocationHistoryEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataAllocationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AllocationHistoryRepositoryAdapter implements AllocationHistoryRepositoryPort {

    private final SpringDataAllocationHistoryRepository repository;

    @Override
    public AllocationHistory save(AllocationHistory history) {
        AllocationHistoryEntity entity = toEntity(history);
        AllocationHistoryEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AllocationHistory> findByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AllocationHistory> findByAllocationId(UUID allocationId) {
        return repository.findByAllocationId(allocationId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private AllocationHistoryEntity toEntity(AllocationHistory history) {
        return AllocationHistoryEntity.builder()
                .id(history.getId())
                .allocationId(history.getAllocationId())
                .studentId(history.getStudentId())
                .actionType(history.getActionType())
                .fromBedId(history.getFromBedId())
                .toBedId(history.getToBedId())
                .reason(history.getReason())
                .performedBy(history.getPerformedBy())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private AllocationHistory toDomain(AllocationHistoryEntity entity) {
        return AllocationHistory.builder()
                .id(entity.getId())
                .allocationId(entity.getAllocationId())
                .studentId(entity.getStudentId())
                .actionType(entity.getActionType())
                .fromBedId(entity.getFromBedId())
                .toBedId(entity.getToBedId())
                .reason(entity.getReason())
                .performedBy(entity.getPerformedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

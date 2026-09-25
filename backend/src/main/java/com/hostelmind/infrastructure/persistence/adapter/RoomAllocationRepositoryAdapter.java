package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AllocationStatus;
import com.hostelmind.domain.model.RoomAllocation;
import com.hostelmind.domain.port.RoomAllocationRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.RoomAllocationEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataRoomAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomAllocationRepositoryAdapter implements RoomAllocationRepositoryPort {

    private final SpringDataRoomAllocationRepository repository;

    @Override
    public RoomAllocation save(RoomAllocation allocation) {
        RoomAllocationEntity entity = toEntity(allocation);
        RoomAllocationEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RoomAllocation> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<RoomAllocation> findActiveByStudentId(UUID studentId) {
        return repository.findByStudentIdAndStatus(studentId, AllocationStatus.ACTIVE).map(this::toDomain);
    }

    @Override
    public Optional<RoomAllocation> findActiveByBedId(UUID bedId) {
        return repository.findByBedIdAndStatus(bedId, AllocationStatus.ACTIVE).map(this::toDomain);
    }

    @Override
    public List<RoomAllocation> findByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<RoomAllocation> findByStatus(AllocationStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<RoomAllocation> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private RoomAllocationEntity toEntity(RoomAllocation allocation) {
        return RoomAllocationEntity.builder()
                .id(allocation.getId())
                .studentId(allocation.getStudentId())
                .bedId(allocation.getBedId())
                .roomId(allocation.getRoomId())
                .academicYear(allocation.getAcademicYear())
                .startDate(allocation.getStartDate())
                .endDate(allocation.getEndDate())
                .status(allocation.getStatus())
                .allocatedBy(allocation.getAllocatedBy())
                .createdAt(allocation.getCreatedAt())
                .updatedAt(allocation.getUpdatedAt())
                .build();
    }

    private RoomAllocation toDomain(RoomAllocationEntity entity) {
        return RoomAllocation.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .bedId(entity.getBedId())
                .roomId(entity.getRoomId())
                .academicYear(entity.getAcademicYear())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .allocatedBy(entity.getAllocatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

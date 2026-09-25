package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.AllocationStatus;
import com.hostelmind.infrastructure.persistence.entity.RoomAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRoomAllocationRepository extends JpaRepository<RoomAllocationEntity, UUID> {
    Optional<RoomAllocationEntity> findByStudentIdAndStatus(UUID studentId, AllocationStatus status);
    Optional<RoomAllocationEntity> findByBedIdAndStatus(UUID bedId, AllocationStatus status);
    List<RoomAllocationEntity> findByStudentId(UUID studentId);
    List<RoomAllocationEntity> findByStatus(AllocationStatus status);
}

package com.hostelmind.domain.port;

import com.hostelmind.domain.model.AllocationStatus;
import com.hostelmind.domain.model.RoomAllocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomAllocationRepositoryPort {
    RoomAllocation save(RoomAllocation allocation);
    Optional<RoomAllocation> findById(UUID id);
    Optional<RoomAllocation> findActiveByStudentId(UUID studentId);
    Optional<RoomAllocation> findActiveByBedId(UUID bedId);
    List<RoomAllocation> findByStudentId(UUID studentId);
    List<RoomAllocation> findByStatus(AllocationStatus status);
    List<RoomAllocation> findAll();
}

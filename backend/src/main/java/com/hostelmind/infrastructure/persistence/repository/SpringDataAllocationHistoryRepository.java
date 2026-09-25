package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.AllocationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAllocationHistoryRepository extends JpaRepository<AllocationHistoryEntity, UUID> {
    List<AllocationHistoryEntity> findByStudentId(UUID studentId);
    List<AllocationHistoryEntity> findByAllocationId(UUID allocationId);
}

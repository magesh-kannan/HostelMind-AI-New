package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.ComplaintStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataComplaintStatusHistoryRepository
        extends JpaRepository<ComplaintStatusHistoryEntity, UUID> {
    List<ComplaintStatusHistoryEntity> findByComplaintIdOrderByChangedAtAsc(UUID complaintId);
}

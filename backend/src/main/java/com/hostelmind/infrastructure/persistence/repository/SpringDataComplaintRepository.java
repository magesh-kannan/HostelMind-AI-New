package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintStatus;
import com.hostelmind.infrastructure.persistence.entity.ComplaintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataComplaintRepository extends JpaRepository<ComplaintEntity, UUID> {
    List<ComplaintEntity> findByStudentId(UUID studentId);
    List<ComplaintEntity> findByHostelId(UUID hostelId);
    List<ComplaintEntity> findByStatus(ComplaintStatus status);
    List<ComplaintEntity> findByAssignedToId(UUID assignedToId);
    List<ComplaintEntity> findByStudentIdAndStatus(UUID studentId, ComplaintStatus status);
    List<ComplaintEntity> findByCategory(ComplaintCategory category);
    long countByStatus(ComplaintStatus status);
    long countByHostelIdAndStatus(UUID hostelId, ComplaintStatus status);
}

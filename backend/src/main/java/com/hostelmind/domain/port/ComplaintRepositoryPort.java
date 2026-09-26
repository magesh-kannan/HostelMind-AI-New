package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Complaint;
import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintRepositoryPort {
    Complaint save(Complaint complaint);
    Optional<Complaint> findById(UUID id);
    List<Complaint> findByStudentId(UUID studentId);
    List<Complaint> findByHostelId(UUID hostelId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByAssignedToId(UUID assignedToId);
    List<Complaint> findByStudentIdAndStatus(UUID studentId, ComplaintStatus status);
    List<Complaint> findByCategory(ComplaintCategory category);
    long countByStatus(ComplaintStatus status);
    long countByHostelIdAndStatus(UUID hostelId, ComplaintStatus status);
    void deleteById(UUID id);
}

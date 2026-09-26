package com.hostelmind.domain.port;

import com.hostelmind.domain.model.ComplaintStatusHistory;

import java.util.List;
import java.util.UUID;

public interface ComplaintStatusHistoryRepositoryPort {
    ComplaintStatusHistory save(ComplaintStatusHistory history);
    List<ComplaintStatusHistory> findByComplaintId(UUID complaintId);
}

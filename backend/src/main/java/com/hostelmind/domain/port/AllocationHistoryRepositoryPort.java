package com.hostelmind.domain.port;

import com.hostelmind.domain.model.AllocationHistory;

import java.util.List;
import java.util.UUID;

public interface AllocationHistoryRepositoryPort {
    AllocationHistory save(AllocationHistory history);
    List<AllocationHistory> findByStudentId(UUID studentId);
    List<AllocationHistory> findByAllocationId(UUID allocationId);
}

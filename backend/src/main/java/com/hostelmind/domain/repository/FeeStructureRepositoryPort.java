package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.FeeStructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeStructureRepositoryPort {
    FeeStructure save(FeeStructure feeStructure);
    Optional<FeeStructure> findById(UUID id);
    Optional<FeeStructure> findByHostelRoomTypeAndYear(UUID hostelId, String roomType, String academicYear);
    List<FeeStructure> findByHostelId(UUID hostelId);
    List<FeeStructure> findAll();
    void deleteById(UUID id);
}

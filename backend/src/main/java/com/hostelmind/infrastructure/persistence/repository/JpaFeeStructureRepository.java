package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.FeeStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFeeStructureRepository extends JpaRepository<FeeStructureEntity, UUID> {
    Optional<FeeStructureEntity> findByHostelIdAndRoomTypeAndAcademicYear(UUID hostelId, String roomType, String academicYear);
    List<FeeStructureEntity> findByHostelId(UUID hostelId);
}

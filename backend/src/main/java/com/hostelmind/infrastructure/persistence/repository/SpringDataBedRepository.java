package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.BedStatus;
import com.hostelmind.infrastructure.persistence.entity.BedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataBedRepository extends JpaRepository<BedEntity, UUID> {
    List<BedEntity> findByRoomId(UUID roomId);
    List<BedEntity> findByRoomIdAndStatus(UUID roomId, BedStatus status);
    long countByStatus(BedStatus status);
}

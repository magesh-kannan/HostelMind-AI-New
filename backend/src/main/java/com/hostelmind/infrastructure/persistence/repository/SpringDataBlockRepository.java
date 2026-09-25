package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataBlockRepository extends JpaRepository<BlockEntity, UUID> {
    List<BlockEntity> findByHostelId(UUID hostelId);
}

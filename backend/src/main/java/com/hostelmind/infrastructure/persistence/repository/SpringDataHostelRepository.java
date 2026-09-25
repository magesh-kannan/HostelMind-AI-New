package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.HostelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataHostelRepository extends JpaRepository<HostelEntity, UUID> {
    List<HostelEntity> findByCampusId(UUID campusId);
}

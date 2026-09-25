package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.CampusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCampusRepository extends JpaRepository<CampusEntity, UUID> {
    Optional<CampusEntity> findByCode(String code);
}

package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.AssetCategory;
import com.hostelmind.domain.model.AssetStatus;
import com.hostelmind.infrastructure.persistence.entity.FacilityAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaFacilityAssetRepository extends JpaRepository<FacilityAssetEntity, UUID> {
    List<FacilityAssetEntity> findByHostelId(UUID hostelId);
    List<FacilityAssetEntity> findByStatus(AssetStatus status);
    List<FacilityAssetEntity> findByCategory(AssetCategory category);
}

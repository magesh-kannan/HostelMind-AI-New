package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.AssetCategory;
import com.hostelmind.domain.model.AssetStatus;
import com.hostelmind.domain.model.FacilityAsset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacilityAssetRepositoryPort {
    FacilityAsset save(FacilityAsset asset);
    Optional<FacilityAsset> findById(UUID id);
    List<FacilityAsset> findByHostelId(UUID hostelId);
    List<FacilityAsset> findByStatus(AssetStatus status);
    List<FacilityAsset> findByCategory(AssetCategory category);
    List<FacilityAsset> findAll();
    void deleteById(UUID id);
}

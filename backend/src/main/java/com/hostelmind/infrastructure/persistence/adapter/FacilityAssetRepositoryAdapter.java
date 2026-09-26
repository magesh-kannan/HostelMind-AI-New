package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AssetCategory;
import com.hostelmind.domain.model.AssetStatus;
import com.hostelmind.domain.model.FacilityAsset;
import com.hostelmind.domain.repository.FacilityAssetRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.FacilityAssetEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaFacilityAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacilityAssetRepositoryAdapter implements FacilityAssetRepositoryPort {

    private final JpaFacilityAssetRepository jpaRepository;

    @Override
    public FacilityAsset save(FacilityAsset domain) {
        FacilityAssetEntity entity = toEntity(domain);
        FacilityAssetEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FacilityAsset> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<FacilityAsset> findByHostelId(UUID hostelId) {
        return jpaRepository.findByHostelId(hostelId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FacilityAsset> findByStatus(AssetStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FacilityAsset> findByCategory(AssetCategory category) {
        return jpaRepository.findByCategory(category).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FacilityAsset> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private FacilityAssetEntity toEntity(FacilityAsset domain) {
        return FacilityAssetEntity.builder()
                .id(domain.getId())
                .hostelId(domain.getHostelId())
                .name(domain.getName())
                .category(domain.getCategory())
                .location(domain.getLocation())
                .status(domain.getStatus())
                .lastInspectedAt(domain.getLastInspectedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private FacilityAsset toDomain(FacilityAssetEntity entity) {
        return FacilityAsset.builder()
                .id(entity.getId())
                .hostelId(entity.getHostelId())
                .name(entity.getName())
                .category(entity.getCategory())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .lastInspectedAt(entity.getLastInspectedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Campus;
import com.hostelmind.domain.port.CampusRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.CampusEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataCampusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampusRepositoryAdapter implements CampusRepositoryPort {

    private final SpringDataCampusRepository repository;

    @Override
    public Campus save(Campus campus) {
        CampusEntity entity = toEntity(campus);
        CampusEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Campus> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Campus> findByCode(String code) {
        return repository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<Campus> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private CampusEntity toEntity(Campus campus) {
        return CampusEntity.builder()
                .id(campus.getId())
                .name(campus.getName())
                .code(campus.getCode())
                .address(campus.getAddress())
                .createdAt(campus.getCreatedAt())
                .updatedAt(campus.getUpdatedAt())
                .build();
    }

    private Campus toDomain(CampusEntity entity) {
        return Campus.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .address(entity.getAddress())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

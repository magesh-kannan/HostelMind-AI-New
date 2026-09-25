package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Hostel;
import com.hostelmind.domain.port.HostelRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.HostelEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataHostelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HostelRepositoryAdapter implements HostelRepositoryPort {

    private final SpringDataHostelRepository repository;

    @Override
    public Hostel save(Hostel hostel) {
        HostelEntity entity = toEntity(hostel);
        HostelEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Hostel> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Hostel> findByCampusId(UUID campusId) {
        return repository.findByCampusId(campusId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Hostel> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return repository.count();
    }

    private HostelEntity toEntity(Hostel hostel) {
        return HostelEntity.builder()
                .id(hostel.getId())
                .campusId(hostel.getCampusId())
                .name(hostel.getName())
                .genderType(hostel.getGenderType())
                .wardenId(hostel.getWardenId())
                .createdAt(hostel.getCreatedAt())
                .updatedAt(hostel.getUpdatedAt())
                .build();
    }

    private Hostel toDomain(HostelEntity entity) {
        return Hostel.builder()
                .id(entity.getId())
                .campusId(entity.getCampusId())
                .name(entity.getName())
                .genderType(entity.getGenderType())
                .wardenId(entity.getWardenId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

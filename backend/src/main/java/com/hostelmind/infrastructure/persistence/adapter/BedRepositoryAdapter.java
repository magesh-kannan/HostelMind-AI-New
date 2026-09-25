package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Bed;
import com.hostelmind.domain.model.BedStatus;
import com.hostelmind.domain.port.BedRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.BedEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataBedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BedRepositoryAdapter implements BedRepositoryPort {

    private final SpringDataBedRepository repository;

    @Override
    public Bed save(Bed bed) {
        BedEntity entity = toEntity(bed);
        BedEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Bed> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Bed> findByRoomId(UUID roomId) {
        return repository.findByRoomId(roomId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Bed> findByRoomIdAndStatus(UUID roomId, BedStatus status) {
        return repository.findByRoomIdAndStatus(roomId, status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByStatus(BedStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public long count() {
        return repository.count();
    }

    private BedEntity toEntity(Bed bed) {
        return BedEntity.builder()
                .id(bed.getId())
                .roomId(bed.getRoomId())
                .bedNumber(bed.getBedNumber())
                .status(bed.getStatus())
                .createdAt(bed.getCreatedAt())
                .updatedAt(bed.getUpdatedAt())
                .build();
    }

    private Bed toDomain(BedEntity entity) {
        return Bed.builder()
                .id(entity.getId())
                .roomId(entity.getRoomId())
                .bedNumber(entity.getBedNumber())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

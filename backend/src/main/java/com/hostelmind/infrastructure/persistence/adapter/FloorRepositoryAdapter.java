package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Floor;
import com.hostelmind.domain.port.FloorRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.FloorEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataFloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FloorRepositoryAdapter implements FloorRepositoryPort {

    private final SpringDataFloorRepository repository;

    @Override
    public Floor save(Floor floor) {
        FloorEntity entity = toEntity(floor);
        FloorEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Floor> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Floor> findByBlockId(UUID blockId) {
        return repository.findByBlockId(blockId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private FloorEntity toEntity(Floor floor) {
        return FloorEntity.builder()
                .id(floor.getId())
                .blockId(floor.getBlockId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .createdAt(floor.getCreatedAt())
                .updatedAt(floor.getUpdatedAt())
                .build();
    }

    private Floor toDomain(FloorEntity entity) {
        return Floor.builder()
                .id(entity.getId())
                .blockId(entity.getBlockId())
                .floorNumber(entity.getFloorNumber())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

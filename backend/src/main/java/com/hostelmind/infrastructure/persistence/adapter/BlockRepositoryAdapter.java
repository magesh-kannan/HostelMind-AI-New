package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Block;
import com.hostelmind.domain.port.BlockRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.BlockEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BlockRepositoryAdapter implements BlockRepositoryPort {

    private final SpringDataBlockRepository repository;

    @Override
    public Block save(Block block) {
        BlockEntity entity = toEntity(block);
        BlockEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Block> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Block> findByHostelId(UUID hostelId) {
        return repository.findByHostelId(hostelId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BlockEntity toEntity(Block block) {
        return BlockEntity.builder()
                .id(block.getId())
                .hostelId(block.getHostelId())
                .name(block.getName())
                .code(block.getCode())
                .totalFloors(block.getTotalFloors())
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }

    private Block toDomain(BlockEntity entity) {
        return Block.builder()
                .id(entity.getId())
                .hostelId(entity.getHostelId())
                .name(entity.getName())
                .code(entity.getCode())
                .totalFloors(entity.getTotalFloors())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

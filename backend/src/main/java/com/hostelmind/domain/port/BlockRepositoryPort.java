package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Block;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockRepositoryPort {
    Block save(Block block);
    Optional<Block> findById(UUID id);
    List<Block> findByHostelId(UUID hostelId);
}

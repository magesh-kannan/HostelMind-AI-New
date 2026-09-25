package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Floor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FloorRepositoryPort {
    Floor save(Floor floor);
    Optional<Floor> findById(UUID id);
    List<Floor> findByBlockId(UUID blockId);
}

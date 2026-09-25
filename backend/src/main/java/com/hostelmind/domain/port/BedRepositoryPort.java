package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Bed;
import com.hostelmind.domain.model.BedStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedRepositoryPort {
    Bed save(Bed bed);
    Optional<Bed> findById(UUID id);
    List<Bed> findByRoomId(UUID roomId);
    List<Bed> findByRoomIdAndStatus(UUID roomId, BedStatus status);
    long countByStatus(BedStatus status);
    long count();
}

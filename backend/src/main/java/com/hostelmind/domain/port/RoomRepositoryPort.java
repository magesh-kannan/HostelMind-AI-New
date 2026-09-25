package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Room;
import com.hostelmind.domain.model.RoomStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepositoryPort {
    Room save(Room room);
    Optional<Room> findById(UUID id);
    List<Room> findByFloorId(UUID floorId);
    List<Room> findByStatus(RoomStatus status);
    List<Room> findAll();
    long count();
}

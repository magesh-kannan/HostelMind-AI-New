package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Room;
import com.hostelmind.domain.model.RoomStatus;
import com.hostelmind.domain.port.RoomRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.RoomEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomRepositoryAdapter implements RoomRepositoryPort {

    private final SpringDataRoomRepository repository;

    @Override
    public Room save(Room room) {
        RoomEntity entity = toEntity(room);
        RoomEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Room> findByFloorId(UUID floorId) {
        return repository.findByFloorId(floorId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Room> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return repository.count();
    }

    private RoomEntity toEntity(Room room) {
        return RoomEntity.builder()
                .id(room.getId())
                .floorId(room.getFloorId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .occupiedCount(room.getOccupiedCount())
                .status(room.getStatus())
                .monthlyRent(room.getMonthlyRent())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    private Room toDomain(RoomEntity entity) {
        return Room.builder()
                .id(entity.getId())
                .floorId(entity.getFloorId())
                .roomNumber(entity.getRoomNumber())
                .roomType(entity.getRoomType())
                .capacity(entity.getCapacity())
                .occupiedCount(entity.getOccupiedCount())
                .status(entity.getStatus())
                .monthlyRent(entity.getMonthlyRent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

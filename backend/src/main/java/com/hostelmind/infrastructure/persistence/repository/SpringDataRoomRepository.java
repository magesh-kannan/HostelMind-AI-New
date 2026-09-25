package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.RoomStatus;
import com.hostelmind.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataRoomRepository extends JpaRepository<RoomEntity, UUID> {
    List<RoomEntity> findByFloorId(UUID floorId);
    List<RoomEntity> findByStatus(RoomStatus status);
}

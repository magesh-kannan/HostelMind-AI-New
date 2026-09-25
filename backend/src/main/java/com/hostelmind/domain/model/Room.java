package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private UUID id;
    private UUID floorId;
    private String roomNumber;
    private RoomType roomType;
    private int capacity;
    private int occupiedCount;
    private RoomStatus status;
    private BigDecimal monthlyRent;
    private Instant createdAt;
    private Instant updatedAt;
}

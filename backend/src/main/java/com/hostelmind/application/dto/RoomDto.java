package com.hostelmind.application.dto;

import com.hostelmind.domain.model.RoomStatus;
import com.hostelmind.domain.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private UUID id;
    private UUID floorId;
    private String roomNumber;
    private RoomType roomType;
    private int capacity;
    private int occupiedCount;
    private RoomStatus status;
    private BigDecimal monthlyRent;
    private List<BedDto> beds;
}

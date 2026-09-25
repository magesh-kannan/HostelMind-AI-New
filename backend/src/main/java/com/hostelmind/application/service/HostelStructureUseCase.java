package com.hostelmind.application.service;

import com.hostelmind.application.dto.*;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.exception.ResourceNotFoundException;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelStructureUseCase {

    private final CampusRepositoryPort campusRepository;
    private final HostelRepositoryPort hostelRepository;
    private final BlockRepositoryPort blockRepository;
    private final FloorRepositoryPort floorRepository;
    private final RoomRepositoryPort roomRepository;
    private final BedRepositoryPort bedRepository;

    @Transactional
    public CampusDto createCampus(CreateCampusRequest request) {
        if (campusRepository.findByCode(request.getCode()).isPresent()) {
            throw new DomainException("Campus code already exists: " + request.getCode());
        }
        Campus campus = Campus.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .address(request.getAddress())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapCampusToDto(campusRepository.save(campus));
    }

    public List<CampusDto> getAllCampuses() {
        return campusRepository.findAll().stream().map(this::mapCampusToDto).collect(Collectors.toList());
    }

    @Transactional
    public HostelDto createHostel(CreateHostelRequest request) {
        campusRepository.findById(request.getCampusId())
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found"));

        Hostel hostel = Hostel.builder()
                .campusId(request.getCampusId())
                .name(request.getName())
                .genderType(request.getGenderType())
                .wardenId(request.getWardenId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return mapHostelToDto(hostelRepository.save(hostel));
    }

    public List<HostelDto> getHostelsByCampus(UUID campusId) {
        return hostelRepository.findByCampusId(campusId).stream().map(this::mapHostelToDto).collect(Collectors.toList());
    }

    public List<HostelDto> getAllHostels() {
        return hostelRepository.findAll().stream().map(this::mapHostelToDto).collect(Collectors.toList());
    }

    @Transactional
    public BlockDto createBlock(CreateBlockRequest request) {
        hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found"));

        Block block = Block.builder()
                .hostelId(request.getHostelId())
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .totalFloors(request.getTotalFloors())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        Block savedBlock = blockRepository.save(block);

        // Auto-generate floor records for convenience
        for (int i = 0; i <= request.getTotalFloors(); i++) {
            Floor floor = Floor.builder()
                    .blockId(savedBlock.getId())
                    .floorNumber(i)
                    .name(i == 0 ? "Ground Floor" : "Floor " + i)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            floorRepository.save(floor);
        }

        return mapBlockToDto(savedBlock);
    }

    public List<BlockDto> getBlocksByHostel(UUID hostelId) {
        return blockRepository.findByHostelId(hostelId).stream().map(this::mapBlockToDto).collect(Collectors.toList());
    }

    public List<FloorDto> getFloorsByBlock(UUID blockId) {
        return floorRepository.findByBlockId(blockId).stream().map(this::mapFloorToDto).collect(Collectors.toList());
    }

    @Transactional
    public RoomDto createRoom(CreateRoomRequest request) {
        floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));

        Room room = Room.builder()
                .floorId(request.getFloorId())
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .capacity(request.getCapacity())
                .occupiedCount(0)
                .status(RoomStatus.AVAILABLE)
                .monthlyRent(request.getMonthlyRent() != null ? request.getMonthlyRent() : BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Room savedRoom = roomRepository.save(room);

        // Auto-generate beds according to capacity (Bed A, Bed B, Bed C...)
        for (int i = 0; i < request.getCapacity(); i++) {
            char bedLetter = (char) ('A' + i);
            Bed bed = Bed.builder()
                    .roomId(savedRoom.getId())
                    .bedNumber("Bed " + bedLetter)
                    .status(BedStatus.VACANT)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            bedRepository.save(bed);
        }

        return mapRoomToDto(savedRoom);
    }

    public List<RoomDto> getRoomsByFloor(UUID floorId) {
        return roomRepository.findByFloorId(floorId).stream().map(this::mapRoomToDto).collect(Collectors.toList());
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream().map(this::mapRoomToDto).collect(Collectors.toList());
    }

    private CampusDto mapCampusToDto(Campus campus) {
        return CampusDto.builder()
                .id(campus.getId())
                .name(campus.getName())
                .code(campus.getCode())
                .address(campus.getAddress())
                .build();
    }

    private HostelDto mapHostelToDto(Hostel hostel) {
        return HostelDto.builder()
                .id(hostel.getId())
                .campusId(hostel.getCampusId())
                .name(hostel.getName())
                .genderType(hostel.getGenderType())
                .wardenId(hostel.getWardenId())
                .build();
    }

    private BlockDto mapBlockToDto(Block block) {
        return BlockDto.builder()
                .id(block.getId())
                .hostelId(block.getHostelId())
                .name(block.getName())
                .code(block.getCode())
                .totalFloors(block.getTotalFloors())
                .build();
    }

    private FloorDto mapFloorToDto(Floor floor) {
        return FloorDto.builder()
                .id(floor.getId())
                .blockId(floor.getBlockId())
                .floorNumber(floor.getFloorNumber())
                .name(floor.getName())
                .build();
    }

    public RoomDto mapRoomToDto(Room room) {
        List<BedDto> bedDtos = bedRepository.findByRoomId(room.getId())
                .stream()
                .map(bed -> BedDto.builder()
                        .id(bed.getId())
                        .roomId(bed.getRoomId())
                        .bedNumber(bed.getBedNumber())
                        .status(bed.getStatus())
                        .build())
                .collect(Collectors.toList());

        return RoomDto.builder()
                .id(room.getId())
                .floorId(room.getFloorId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .occupiedCount(room.getOccupiedCount())
                .status(room.getStatus())
                .monthlyRent(room.getMonthlyRent())
                .beds(bedDtos)
                .build();
    }
}

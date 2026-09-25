package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.*;
import com.hostelmind.application.service.HostelStructureUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hostels")
@RequiredArgsConstructor
public class HostelController {

    private final HostelStructureUseCase hostelStructureUseCase;

    // Campuses
    @PostMapping("/campuses")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<CampusDto> createCampus(@Valid @RequestBody CreateCampusRequest request) {
        CampusDto campus = hostelStructureUseCase.createCampus(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(campus);
    }

    @GetMapping("/campuses")
    public ResponseEntity<List<CampusDto>> getAllCampuses() {
        return ResponseEntity.ok(hostelStructureUseCase.getAllCampuses());
    }

    // Hostels
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<HostelDto> createHostel(@Valid @RequestBody CreateHostelRequest request) {
        HostelDto hostel = hostelStructureUseCase.createHostel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(hostel);
    }

    @GetMapping
    public ResponseEntity<List<HostelDto>> getAllHostels() {
        return ResponseEntity.ok(hostelStructureUseCase.getAllHostels());
    }

    // Blocks
    @PostMapping("/blocks")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<BlockDto> createBlock(@Valid @RequestBody CreateBlockRequest request) {
        BlockDto block = hostelStructureUseCase.createBlock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(block);
    }

    @GetMapping("/{hostelId}/blocks")
    public ResponseEntity<List<BlockDto>> getBlocksByHostel(@PathVariable UUID hostelId) {
        return ResponseEntity.ok(hostelStructureUseCase.getBlocksByHostel(hostelId));
    }

    // Floors
    @GetMapping("/blocks/{blockId}/floors")
    public ResponseEntity<List<FloorDto>> getFloorsByBlock(@PathVariable UUID blockId) {
        return ResponseEntity.ok(hostelStructureUseCase.getFloorsByBlock(blockId));
    }

    // Rooms
    @PostMapping("/rooms")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomDto room = hostelStructureUseCase.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping("/floors/{floorId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByFloor(@PathVariable UUID floorId) {
        return ResponseEntity.ok(hostelStructureUseCase.getRoomsByFloor(floorId));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(hostelStructureUseCase.getAllRooms());
    }
}

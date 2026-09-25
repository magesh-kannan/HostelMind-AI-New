package com.hostelmind.interface_layer.controller;

import com.hostelmind.application.dto.*;
import com.hostelmind.application.service.RoomAllocationUseCase;
import com.hostelmind.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final RoomAllocationUseCase allocationUseCase;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<RoomAllocationDto> allocateBed(
            @Valid @RequestBody AllocateBedRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        RoomAllocationDto dto = allocationUseCase.allocateBed(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<RoomAllocationDto> transferBed(
            @Valid @RequestBody TransferBedRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        RoomAllocationDto dto = allocationUseCase.transferBed(request, principal.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/vacate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WARDEN')")
    public ResponseEntity<RoomAllocationDto> vacateBed(
            @Valid @RequestBody VacateBedRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        RoomAllocationDto dto = allocationUseCase.vacateBed(request, principal.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<RoomAllocationDto>> getAllAllocations() {
        return ResponseEntity.ok(allocationUseCase.getAllAllocations());
    }

    @GetMapping("/stats")
    public ResponseEntity<OccupancyStatsDto> getOccupancyStats() {
        return ResponseEntity.ok(allocationUseCase.getOccupancyStats());
    }
}

package com.hostelmind.application.service;

import com.hostelmind.application.dto.*;
import com.hostelmind.domain.exception.DomainException;
import com.hostelmind.domain.exception.ResourceNotFoundException;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomAllocationUseCase {

    private final UserRepositoryPort userRepository;
    private final BedRepositoryPort bedRepository;
    private final RoomRepositoryPort roomRepository;
    private final HostelRepositoryPort hostelRepository;
    private final RoomAllocationRepositoryPort allocationRepository;
    private final AllocationHistoryRepositoryPort historyRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    @Transactional
    public RoomAllocationDto allocateBed(AllocateBedRequest request, UUID performerId) {
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Guard 1: Duplicate active allocation check
        if (allocationRepository.findActiveByStudentId(request.getStudentId()).isPresent()) {
            throw new DomainException("Student already has an active room allocation");
        }

        // Guard 2: Bed existence and status check
        Bed bed = bedRepository.findById(request.getBedId())
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found"));

        if (bed.getStatus() != BedStatus.VACANT) {
            throw new DomainException("Bed " + bed.getBedNumber() + " is not vacant for allocation");
        }

        // Guard 3: Room capacity check
        Room room = roomRepository.findById(bed.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new DomainException("Room " + room.getRoomNumber() + " capacity exceeded");
        }

        // 1. Update Bed
        bed.setStatus(BedStatus.OCCUPIED);
        bed.setUpdatedAt(Instant.now());
        bedRepository.save(bed);

        // 2. Update Room
        room.setOccupiedCount(room.getOccupiedCount() + 1);
        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus(RoomStatus.OCCUPIED);
        }
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        // 3. Create Allocation
        RoomAllocation allocation = RoomAllocation.builder()
                .id(UUID.randomUUID())
                .studentId(student.getId())
                .bedId(bed.getId())
                .roomId(room.getId())
                .academicYear(request.getAcademicYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(AllocationStatus.ACTIVE)
                .allocatedBy(performerId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        RoomAllocation savedAllocation = allocationRepository.save(allocation);

        // 4. Record Allocation History
        AllocationHistory history = AllocationHistory.builder()
                .allocationId(savedAllocation.getId())
                .studentId(student.getId())
                .actionType(AllocationActionType.ALLOCATED)
                .toBedId(bed.getId())
                .reason("Initial Room Allocation")
                .performedBy(performerId)
                .createdAt(Instant.now())
                .build();
        historyRepository.save(history);

        // 5. Audit Log
        auditLogRepository.save(AuditLog.builder()
                .userId(performerId)
                .action("ROOM_ALLOCATE")
                .entityType("RoomAllocation")
                .entityId(savedAllocation.getId().toString())
                .details("Allocated Bed " + bed.getBedNumber() + " in Room " + room.getRoomNumber() + " to student: " + student.getEmail())
                .createdAt(Instant.now())
                .build());

        return mapToDto(savedAllocation, student, bed, room);
    }

    @Transactional
    public RoomAllocationDto transferBed(TransferBedRequest request, UUID performerId) {
        RoomAllocation allocation = allocationRepository.findById(request.getAllocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found"));

        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new DomainException("Only active allocations can be transferred");
        }

        Bed newBed = bedRepository.findById(request.getNewBedId())
                .orElseThrow(() -> new ResourceNotFoundException("Target bed not found"));

        if (newBed.getStatus() != BedStatus.VACANT) {
            throw new DomainException("Target bed is not vacant for transfer");
        }

        Room newRoom = roomRepository.findById(newBed.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Target room not found"));

        if (newRoom.getOccupiedCount() >= newRoom.getCapacity()) {
            throw new DomainException("Target room capacity exceeded");
        }

        Bed oldBed = bedRepository.findById(allocation.getBedId()).orElseThrow();
        Room oldRoom = roomRepository.findById(allocation.getRoomId()).orElseThrow();

        // 1. Vacate old bed & update old room count
        oldBed.setStatus(BedStatus.VACANT);
        oldBed.setUpdatedAt(Instant.now());
        bedRepository.save(oldBed);

        oldRoom.setOccupiedCount(Math.max(0, oldRoom.getOccupiedCount() - 1));
        if (oldRoom.getStatus() == RoomStatus.OCCUPIED && oldRoom.getOccupiedCount() < oldRoom.getCapacity()) {
            oldRoom.setStatus(RoomStatus.AVAILABLE);
        }
        oldRoom.setUpdatedAt(Instant.now());
        roomRepository.save(oldRoom);

        // 2. Occupy new bed & update new room count
        newBed.setStatus(BedStatus.OCCUPIED);
        newBed.setUpdatedAt(Instant.now());
        bedRepository.save(newBed);

        newRoom.setOccupiedCount(newRoom.getOccupiedCount() + 1);
        if (newRoom.getOccupiedCount() >= newRoom.getCapacity()) {
            newRoom.setStatus(RoomStatus.OCCUPIED);
        }
        newRoom.setUpdatedAt(Instant.now());
        roomRepository.save(newRoom);

        // 3. Update Allocation
        UUID previousBedId = allocation.getBedId();
        allocation.setBedId(newBed.getId());
        allocation.setRoomId(newRoom.getId());
        allocation.setUpdatedAt(Instant.now());
        RoomAllocation updatedAllocation = allocationRepository.save(allocation);

        // 4. Record History
        historyRepository.save(AllocationHistory.builder()
                .allocationId(updatedAllocation.getId())
                .studentId(allocation.getStudentId())
                .actionType(AllocationActionType.TRANSFERRED)
                .fromBedId(previousBedId)
                .toBedId(newBed.getId())
                .reason(request.getReason())
                .performedBy(performerId)
                .createdAt(Instant.now())
                .build());

        User student = userRepository.findById(allocation.getStudentId()).orElseThrow();
        return mapToDto(updatedAllocation, student, newBed, newRoom);
    }

    @Transactional
    public RoomAllocationDto vacateBed(VacateBedRequest request, UUID performerId) {
        RoomAllocation allocation = allocationRepository.findById(request.getAllocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found"));

        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new DomainException("Allocation is already vacated or inactive");
        }

        Bed bed = bedRepository.findById(allocation.getBedId()).orElseThrow();
        Room room = roomRepository.findById(allocation.getRoomId()).orElseThrow();

        // 1. Free Bed
        bed.setStatus(BedStatus.VACANT);
        bed.setUpdatedAt(Instant.now());
        bedRepository.save(bed);

        // 2. Update Room Count
        room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
        if (room.getStatus() == RoomStatus.OCCUPIED && room.getOccupiedCount() < room.getCapacity()) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
        room.setUpdatedAt(Instant.now());
        roomRepository.save(room);

        // 3. Mark Allocation Vacated
        allocation.setStatus(AllocationStatus.VACATED);
        allocation.setEndDate(LocalDate.now());
        allocation.setUpdatedAt(Instant.now());
        RoomAllocation vacatedAllocation = allocationRepository.save(allocation);

        // 4. Record History
        historyRepository.save(AllocationHistory.builder()
                .allocationId(vacatedAllocation.getId())
                .studentId(allocation.getStudentId())
                .actionType(AllocationActionType.VACATED)
                .fromBedId(bed.getId())
                .reason(request.getReason())
                .performedBy(performerId)
                .createdAt(Instant.now())
                .build());

        User student = userRepository.findById(allocation.getStudentId()).orElseThrow();
        return mapToDto(vacatedAllocation, student, bed, room);
    }

    public List<RoomAllocationDto> getAllAllocations() {
        return allocationRepository.findAll().stream().map(allocation -> {
            User student = userRepository.findById(allocation.getStudentId()).orElse(null);
            Bed bed = bedRepository.findById(allocation.getBedId()).orElse(null);
            Room room = roomRepository.findById(allocation.getRoomId()).orElse(null);
            return mapToDto(allocation, student, bed, room);
        }).collect(Collectors.toList());
    }

    public OccupancyStatsDto getOccupancyStats() {
        long totalHostels = hostelRepository.count();
        long totalRooms = roomRepository.count();
        long occupiedBeds = bedRepository.countByStatus(BedStatus.OCCUPIED);
        long vacantBeds = bedRepository.countByStatus(BedStatus.VACANT);
        long totalCapacity = occupiedBeds + vacantBeds;

        double percentage = totalCapacity > 0 ? ((double) occupiedBeds / totalCapacity) * 100.0 : 0.0;

        return OccupancyStatsDto.builder()
                .totalHostels(totalHostels)
                .totalRooms(totalRooms)
                .totalCapacity(totalCapacity)
                .occupiedBeds(occupiedBeds)
                .vacantBeds(vacantBeds)
                .occupancyPercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }

    private RoomAllocationDto mapToDto(RoomAllocation allocation, User student, Bed bed, Room room) {
        return RoomAllocationDto.builder()
                .id(allocation.getId())
                .studentId(allocation.getStudentId())
                .studentName(student != null ? student.getFullName() : "Unknown")
                .studentEmail(student != null ? student.getEmail() : "Unknown")
                .bedId(allocation.getBedId())
                .bedNumber(bed != null ? bed.getBedNumber() : "Unknown")
                .roomId(allocation.getRoomId())
                .roomNumber(room != null ? room.getRoomNumber() : "Unknown")
                .academicYear(allocation.getAcademicYear())
                .startDate(allocation.getStartDate())
                .endDate(allocation.getEndDate())
                .status(allocation.getStatus())
                .allocatedBy(allocation.getAllocatedBy())
                .createdAt(allocation.getCreatedAt())
                .build();
    }
}

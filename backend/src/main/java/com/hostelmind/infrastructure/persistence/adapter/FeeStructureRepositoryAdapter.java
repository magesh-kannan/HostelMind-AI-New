package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.FeeStructure;
import com.hostelmind.domain.repository.FeeStructureRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.FeeStructureEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaFeeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeeStructureRepositoryAdapter implements FeeStructureRepositoryPort {

    private final JpaFeeStructureRepository jpaRepository;

    @Override
    public FeeStructure save(FeeStructure domain) {
        FeeStructureEntity entity = toEntity(domain);
        FeeStructureEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FeeStructure> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<FeeStructure> findByHostelRoomTypeAndYear(UUID hostelId, String roomType, String academicYear) {
        return jpaRepository.findByHostelIdAndRoomTypeAndAcademicYear(hostelId, roomType, academicYear).map(this::toDomain);
    }

    @Override
    public List<FeeStructure> findByHostelId(UUID hostelId) {
        return jpaRepository.findByHostelId(hostelId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<FeeStructure> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private FeeStructureEntity toEntity(FeeStructure domain) {
        return FeeStructureEntity.builder()
                .id(domain.getId())
                .hostelId(domain.getHostelId())
                .roomType(domain.getRoomType())
                .academicYear(domain.getAcademicYear())
                .rentAmount(domain.getRentAmount())
                .utilityDeposit(domain.getUtilityDeposit())
                .messFee(domain.getMessFee())
                .otherCharges(domain.getOtherCharges())
                .dueDayOfMonth(domain.getDueDayOfMonth())
                .lateFeePerDay(domain.getLateFeePerDay())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private FeeStructure toDomain(FeeStructureEntity entity) {
        return FeeStructure.builder()
                .id(entity.getId())
                .hostelId(entity.getHostelId())
                .roomType(entity.getRoomType())
                .academicYear(entity.getAcademicYear())
                .rentAmount(entity.getRentAmount())
                .utilityDeposit(entity.getUtilityDeposit())
                .messFee(entity.getMessFee())
                .otherCharges(entity.getOtherCharges())
                .dueDayOfMonth(entity.getDueDayOfMonth())
                .lateFeePerDay(entity.getLateFeePerDay())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

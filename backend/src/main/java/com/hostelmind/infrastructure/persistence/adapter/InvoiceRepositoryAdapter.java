package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Invoice;
import com.hostelmind.domain.model.InvoiceStatus;
import com.hostelmind.domain.repository.InvoiceRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.InvoiceEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepositoryPort {

    private final JpaInvoiceRepository jpaRepository;

    @Override
    public Invoice save(Invoice domain) {
        InvoiceEntity entity = toEntity(domain);
        InvoiceEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return jpaRepository.findByInvoiceNumber(invoiceNumber).map(this::toDomain);
    }

    @Override
    public List<Invoice> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByHostelId(UUID hostelId) {
        return jpaRepository.findByHostelId(hostelId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return jpaRepository.findByStatus(status).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Invoice> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private InvoiceEntity toEntity(Invoice domain) {
        return InvoiceEntity.builder()
                .id(domain.getId())
                .invoiceNumber(domain.getInvoiceNumber())
                .studentId(domain.getStudentId())
                .hostelId(domain.getHostelId())
                .roomId(domain.getRoomId())
                .academicYear(domain.getAcademicYear())
                .billingPeriod(domain.getBillingPeriod())
                .subtotal(domain.getSubtotal())
                .lateFee(domain.getLateFee())
                .totalAmount(domain.getTotalAmount())
                .paidAmount(domain.getPaidAmount())
                .dueDate(domain.getDueDate())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Invoice toDomain(InvoiceEntity entity) {
        return Invoice.builder()
                .id(entity.getId())
                .invoiceNumber(entity.getInvoiceNumber())
                .studentId(entity.getStudentId())
                .hostelId(entity.getHostelId())
                .roomId(entity.getRoomId())
                .academicYear(entity.getAcademicYear())
                .billingPeriod(entity.getBillingPeriod())
                .subtotal(entity.getSubtotal())
                .lateFee(entity.getLateFee())
                .totalAmount(entity.getTotalAmount())
                .paidAmount(entity.getPaidAmount())
                .dueDate(entity.getDueDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

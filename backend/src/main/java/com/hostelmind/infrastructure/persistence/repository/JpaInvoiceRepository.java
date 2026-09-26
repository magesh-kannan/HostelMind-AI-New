package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.InvoiceStatus;
import com.hostelmind.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, UUID> {
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    List<InvoiceEntity> findByStudentId(UUID studentId);
    List<InvoiceEntity> findByHostelId(UUID hostelId);
    List<InvoiceEntity> findByStatus(InvoiceStatus status);
}

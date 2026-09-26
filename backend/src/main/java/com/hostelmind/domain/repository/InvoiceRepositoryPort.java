package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.Invoice;
import com.hostelmind.domain.model.InvoiceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStudentId(UUID studentId);
    List<Invoice> findByHostelId(UUID hostelId);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findAll();
    void deleteById(UUID id);
}

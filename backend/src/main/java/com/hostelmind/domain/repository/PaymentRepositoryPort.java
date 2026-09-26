package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByTransactionReference(String transactionReference);
    List<Payment> findByInvoiceId(UUID invoiceId);
    List<Payment> findByStudentId(UUID studentId);
    List<Payment> findAll();
}

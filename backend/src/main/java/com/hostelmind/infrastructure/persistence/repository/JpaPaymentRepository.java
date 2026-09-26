package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByTransactionReference(String transactionReference);
    List<PaymentEntity> findByInvoiceId(UUID invoiceId);
    List<PaymentEntity> findByStudentId(UUID studentId);
}

package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.Payment;
import com.hostelmind.domain.repository.PaymentRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.PaymentEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final JpaPaymentRepository jpaRepository;

    @Override
    public Payment save(Payment domain) {
        PaymentEntity entity = toEntity(domain);
        PaymentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByTransactionReference(String transactionReference) {
        return jpaRepository.findByTransactionReference(transactionReference).map(this::toDomain);
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return jpaRepository.findByInvoiceId(invoiceId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private PaymentEntity toEntity(Payment domain) {
        return PaymentEntity.builder()
                .id(domain.getId())
                .invoiceId(domain.getInvoiceId())
                .studentId(domain.getStudentId())
                .amount(domain.getAmount())
                .paymentMethod(domain.getPaymentMethod())
                .transactionReference(domain.getTransactionReference())
                .gatewayProvider(domain.getGatewayProvider())
                .status(domain.getStatus())
                .paidAt(domain.getPaidAt())
                .build();
    }

    private Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .invoiceId(entity.getInvoiceId())
                .studentId(entity.getStudentId())
                .amount(entity.getAmount())
                .paymentMethod(entity.getPaymentMethod())
                .transactionReference(entity.getTransactionReference())
                .gatewayProvider(entity.getGatewayProvider())
                .status(entity.getStatus())
                .paidAt(entity.getPaidAt())
                .build();
    }
}

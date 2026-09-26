package com.hostelmind.application.service;

import com.hostelmind.application.dto.billing.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.FeeStructureRepositoryPort;
import com.hostelmind.domain.repository.InvoiceRepositoryPort;
import com.hostelmind.domain.repository.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingUseCase {

    private final FeeStructureRepositoryPort feeStructureRepository;
    private final InvoiceRepositoryPort invoiceRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    // ─── Fee Structures ─────────────────────────────────────────────────────────

    @Transactional
    public FeeStructureDto createFeeStructure(CreateFeeStructureRequest req) {
        FeeStructure feeStructure = FeeStructure.builder()
                .hostelId(req.getHostelId())
                .roomType(req.getRoomType())
                .academicYear(req.getAcademicYear())
                .rentAmount(req.getRentAmount())
                .utilityDeposit(req.getUtilityDeposit())
                .messFee(req.getMessFee())
                .otherCharges(req.getOtherCharges())
                .dueDayOfMonth(req.getDueDayOfMonth())
                .lateFeePerDay(req.getLateFeePerDay())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        FeeStructure saved = feeStructureRepository.save(feeStructure);

        auditLogRepository.save(AuditLog.builder()
                .action("CREATE_FEE_STRUCTURE")
                .entityType("FeeStructure")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("Created fee structure for hostel=%s, roomType=%s, year=%s", req.getHostelId(), req.getRoomType(), req.getAcademicYear()))
                .createdAt(Instant.now())
                .build());

        return mapToFeeStructureDto(saved);
    }

    public List<FeeStructureDto> getFeeStructures(UUID hostelId) {
        List<FeeStructure> list = hostelId != null
                ? feeStructureRepository.findByHostelId(hostelId)
                : feeStructureRepository.findAll();
        return list.stream().map(this::mapToFeeStructureDto).collect(Collectors.toList());
    }

    // ─── Invoices ──────────────────────────────────────────────────────────────

    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest req) {
        BigDecimal subtotal = req.getRentAmount()
                .add(req.getUtilityDeposit() != null ? req.getUtilityDeposit() : BigDecimal.ZERO)
                .add(req.getMessFee() != null ? req.getMessFee() : BigDecimal.ZERO)
                .add(req.getOtherCharges() != null ? req.getOtherCharges() : BigDecimal.ZERO);

        String invoiceNum = "INV-" + System.currentTimeMillis();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNum)
                .studentId(req.getStudentId())
                .hostelId(req.getHostelId())
                .roomId(req.getRoomId())
                .academicYear(req.getAcademicYear())
                .billingPeriod(req.getBillingPeriod())
                .subtotal(subtotal)
                .lateFee(BigDecimal.ZERO)
                .totalAmount(subtotal)
                .paidAmount(BigDecimal.ZERO)
                .dueDate(req.getDueDate())
                .status(InvoiceStatus.ISSUED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        auditLogRepository.save(AuditLog.builder()
                .userId(req.getStudentId())
                .action("CREATE_INVOICE")
                .entityType("Invoice")
                .entityId(saved.getId() != null ? saved.getId().toString() : null)
                .details(String.format("Issued invoice %s for amount %s", invoiceNum, subtotal))
                .createdAt(Instant.now())
                .build());

        return mapToInvoiceDto(saved);
    }

    public InvoiceDto getInvoiceById(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));
        return mapToInvoiceDto(invoice);
    }

    public List<InvoiceDto> getStudentInvoices(UUID studentId) {
        return invoiceRepository.findByStudentId(studentId).stream()
                .map(this::mapToInvoiceDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDto> getHostelInvoices(UUID hostelId) {
        return invoiceRepository.findByHostelId(hostelId).stream()
                .map(this::mapToInvoiceDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToInvoiceDto)
                .collect(Collectors.toList());
    }

    // ─── Payment Transactions ──────────────────────────────────────────────────

    @Transactional
    public PaymentDto recordPayment(RecordPaymentRequest req) {
        Invoice invoice = invoiceRepository.findById(req.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + req.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already fully paid");
        }

        String txnRef = req.getTransactionReference() != null
                ? req.getTransactionReference()
                : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .invoiceId(invoice.getId())
                .studentId(invoice.getStudentId())
                .amount(req.getAmount())
                .paymentMethod(req.getPaymentMethod() != null ? req.getPaymentMethod() : PaymentMethod.CREDIT_CARD)
                .transactionReference(txnRef)
                .gatewayProvider(req.getGatewayProvider() != null ? req.getGatewayProvider() : PaymentGatewayProvider.STRIPE)
                .status(PaymentStatus.COMPLETED)
                .paidAt(Instant.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Apply payment to invoice domain state machine
        invoice.applyPayment(req.getAmount());
        invoiceRepository.save(invoice);

        auditLogRepository.save(AuditLog.builder()
                .userId(invoice.getStudentId())
                .action("RECORD_PAYMENT")
                .entityType("Payment")
                .entityId(savedPayment.getId() != null ? savedPayment.getId().toString() : null)
                .details(String.format("Recorded payment %s of %s for invoice %s. New status: %s",
                        txnRef, req.getAmount(), invoice.getInvoiceNumber(), invoice.getStatus()))
                .createdAt(Instant.now())
                .build());

        return mapToPaymentDto(savedPayment);
    }

    public List<PaymentDto> getPaymentsForInvoice(UUID invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToPaymentDto)
                .collect(Collectors.toList());
    }

    // ─── Overdue Invoice Processing ────────────────────────────────────────────

    @Transactional
    public void processOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<Invoice> allInvoices = invoiceRepository.findAll();

        for (Invoice inv : allInvoices) {
            if ((inv.getStatus() == InvoiceStatus.ISSUED || inv.getStatus() == InvoiceStatus.PARTIALLY_PAID)
                    && inv.getDueDate().isBefore(today)) {
                
                inv.setStatus(InvoiceStatus.OVERDUE);
                invoiceRepository.save(inv);
            }
        }
    }

    // ─── Dashboard Stats ───────────────────────────────────────────────────────

    public BillingStatsDto getBillingStats() {
        List<Invoice> all = invoiceRepository.findAll();

        BigDecimal totalBilled = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        long paidCount = 0;
        long overdueCount = 0;

        for (Invoice inv : all) {
            if (inv.getTotalAmount() != null) totalBilled = totalBilled.add(inv.getTotalAmount());
            if (inv.getPaidAmount() != null) totalCollected = totalCollected.add(inv.getPaidAmount());
            if (inv.getStatus() == InvoiceStatus.PAID) paidCount++;
            if (inv.getStatus() == InvoiceStatus.OVERDUE) overdueCount++;
        }

        BigDecimal outstanding = totalBilled.subtract(totalCollected).max(BigDecimal.ZERO);

        return BillingStatsDto.builder()
                .totalBilled(totalBilled)
                .totalCollected(totalCollected)
                .totalOutstanding(outstanding)
                .totalInvoicesCount(all.size())
                .paidInvoicesCount(paidCount)
                .overdueInvoicesCount(overdueCount)
                .build();
    }

    // ─── Mapping Helpers ───────────────────────────────────────────────────────

    private FeeStructureDto mapToFeeStructureDto(FeeStructure fs) {
        return FeeStructureDto.builder()
                .id(fs.getId())
                .hostelId(fs.getHostelId())
                .roomType(fs.getRoomType())
                .academicYear(fs.getAcademicYear())
                .rentAmount(fs.getRentAmount())
                .utilityDeposit(fs.getUtilityDeposit())
                .messFee(fs.getMessFee())
                .otherCharges(fs.getOtherCharges())
                .totalFee(fs.getTotalFee())
                .dueDayOfMonth(fs.getDueDayOfMonth())
                .lateFeePerDay(fs.getLateFeePerDay())
                .createdAt(fs.getCreatedAt())
                .updatedAt(fs.getUpdatedAt())
                .build();
    }

    private InvoiceDto mapToInvoiceDto(Invoice inv) {
        return InvoiceDto.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .studentId(inv.getStudentId())
                .hostelId(inv.getHostelId())
                .roomId(inv.getRoomId())
                .academicYear(inv.getAcademicYear())
                .billingPeriod(inv.getBillingPeriod())
                .subtotal(inv.getSubtotal())
                .lateFee(inv.getLateFee())
                .totalAmount(inv.getTotalAmount())
                .paidAmount(inv.getPaidAmount())
                .remainingBalance(inv.getRemainingBalance())
                .dueDate(inv.getDueDate())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    private PaymentDto mapToPaymentDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .invoiceId(p.getInvoiceId())
                .studentId(p.getStudentId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .transactionReference(p.getTransactionReference())
                .gatewayProvider(p.getGatewayProvider())
                .status(p.getStatus())
                .paidAt(p.getPaidAt())
                .build();
    }
}

package com.hostelmind.application.service;

import com.hostelmind.application.dto.billing.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.FeeStructureRepositoryPort;
import com.hostelmind.domain.repository.InvoiceRepositoryPort;
import com.hostelmind.domain.repository.PaymentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingUseCaseTest {

    @Mock
    private FeeStructureRepositoryPort feeStructureRepository;

    @Mock
    private InvoiceRepositoryPort invoiceRepository;

    @Mock
    private PaymentRepositoryPort paymentRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private BillingUseCase billingUseCase;

    private UUID studentId;
    private UUID hostelId;
    private UUID invoiceId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        hostelId = UUID.randomUUID();
        invoiceId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create fee structure successfully")
    void createFeeStructure_Success() {
        CreateFeeStructureRequest req = new CreateFeeStructureRequest(
                hostelId, "DOUBLE", "2026-2027",
                BigDecimal.valueOf(5000), BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2500), BigDecimal.valueOf(500),
                5, BigDecimal.valueOf(50)
        );

        when(feeStructureRepository.save(any())).thenAnswer(inv -> {
            FeeStructure fs = inv.getArgument(0);
            if (fs.getId() == null) fs.setId(UUID.randomUUID());
            return fs;
        });

        var dto = billingUseCase.createFeeStructure(req);

        assertNotNull(dto);
        assertEquals(hostelId, dto.getHostelId());
        assertEquals("DOUBLE", dto.getRoomType());
        assertEquals(BigDecimal.valueOf(9000), dto.getTotalFee()); // 5000+1000+2500+500

        verify(feeStructureRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    @DisplayName("Should issue invoice with calculated subtotal")
    void createInvoice_Success() {
        CreateInvoiceRequest req = new CreateInvoiceRequest(
                studentId, hostelId, UUID.randomUUID(),
                "2026-2027", "Semester 1", LocalDate.now().plusDays(15),
                BigDecimal.valueOf(5000), BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2500), BigDecimal.valueOf(500)
        );

        when(invoiceRepository.save(any())).thenAnswer(inv -> {
            Invoice invoice = inv.getArgument(0);
            if (invoice.getId() == null) invoice.setId(invoiceId);
            return invoice;
        });

        var dto = billingUseCase.createInvoice(req);

        assertNotNull(dto);
        assertEquals(InvoiceStatus.ISSUED, dto.getStatus());
        assertEquals(BigDecimal.valueOf(9000), dto.getSubtotal());
        assertEquals(BigDecimal.valueOf(9000), dto.getTotalAmount());
        assertEquals(BigDecimal.valueOf(0), dto.getPaidAmount());
        assertEquals(BigDecimal.valueOf(9000), dto.getRemainingBalance());
    }

    @Test
    @DisplayName("Should transition status to PARTIALLY_PAID on partial payment")
    void recordPayment_PartialPayment() {
        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .invoiceNumber("INV-1001")
                .studentId(studentId)
                .subtotal(BigDecimal.valueOf(10000))
                .lateFee(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(10000))
                .paidAmount(BigDecimal.ZERO)
                .status(InvoiceStatus.ISSUED)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any())).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            if (p.getId() == null) p.setId(UUID.randomUUID());
            return p;
        });

        RecordPaymentRequest req = new RecordPaymentRequest(
                invoiceId, BigDecimal.valueOf(4000), PaymentMethod.CREDIT_CARD,
                PaymentGatewayProvider.STRIPE, "TXN-998877"
        );

        var paymentDto = billingUseCase.recordPayment(req);

        assertNotNull(paymentDto);
        assertEquals(BigDecimal.valueOf(4000), paymentDto.getAmount());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoice.getStatus());
        assertEquals(BigDecimal.valueOf(4000), invoice.getPaidAmount());
        assertEquals(BigDecimal.valueOf(6000), invoice.getRemainingBalance());
    }

    @Test
    @DisplayName("Should transition status to PAID when full payment is recorded")
    void recordPayment_FullPayment() {
        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .invoiceNumber("INV-1002")
                .studentId(studentId)
                .subtotal(BigDecimal.valueOf(10000))
                .lateFee(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(10000))
                .paidAmount(BigDecimal.ZERO)
                .status(InvoiceStatus.ISSUED)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any())).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            if (p.getId() == null) p.setId(UUID.randomUUID());
            return p;
        });

        RecordPaymentRequest req = new RecordPaymentRequest(
                invoiceId, BigDecimal.valueOf(10000), PaymentMethod.UPI,
                PaymentGatewayProvider.RAZORPAY, "TXN-112233"
        );

        billingUseCase.recordPayment(req);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(BigDecimal.valueOf(10000), invoice.getPaidAmount());
        assertEquals(BigDecimal.ZERO, invoice.getRemainingBalance());
    }

    @Test
    @DisplayName("Should throw exception when attempting payment on already paid invoice")
    void recordPayment_AlreadyPaid_ThrowsException() {
        Invoice invoice = Invoice.builder()
                .id(invoiceId)
                .invoiceNumber("INV-1003")
                .status(InvoiceStatus.PAID)
                .build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        RecordPaymentRequest req = new RecordPaymentRequest(
                invoiceId, BigDecimal.valueOf(1000), PaymentMethod.CASH,
                PaymentGatewayProvider.OFFLINE, "TXN-CASH"
        );

        assertThrows(IllegalStateException.class, () -> billingUseCase.recordPayment(req));
    }

    @Test
    @DisplayName("Should update past-due invoices to OVERDUE status")
    void processOverdueInvoices_Success() {
        Invoice overdueInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .status(InvoiceStatus.ISSUED)
                .dueDate(LocalDate.now().minusDays(5))
                .build();

        when(invoiceRepository.findAll()).thenReturn(List.of(overdueInvoice));

        billingUseCase.processOverdueInvoices();

        assertEquals(InvoiceStatus.OVERDUE, overdueInvoice.getStatus());
        verify(invoiceRepository).save(overdueInvoice);
    }
}

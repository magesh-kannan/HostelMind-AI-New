package com.hostelmind.infrastructure.web.controller;

import com.hostelmind.application.dto.billing.*;
import com.hostelmind.application.service.BillingUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingUseCase billingUseCase;

    // ─── Fee Structures ─────────────────────────────────────────────────────────

    @PostMapping("/fee-structures")
    public ResponseEntity<FeeStructureDto> createFeeStructure(@Valid @RequestBody CreateFeeStructureRequest request) {
        FeeStructureDto created = billingUseCase.createFeeStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/fee-structures")
    public ResponseEntity<List<FeeStructureDto>> getFeeStructures(@RequestParam(required = false) UUID hostelId) {
        return ResponseEntity.ok(billingUseCase.getFeeStructures(hostelId));
    }

    // ─── Invoices ──────────────────────────────────────────────────────────────

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDto created = billingUseCase.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices() {
        return ResponseEntity.ok(billingUseCase.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(billingUseCase.getInvoiceById(id));
    }

    @GetMapping("/invoices/student/{studentId}")
    public ResponseEntity<List<InvoiceDto>> getStudentInvoices(@PathVariable UUID studentId) {
        return ResponseEntity.ok(billingUseCase.getStudentInvoices(studentId));
    }

    @GetMapping("/invoices/hostel/{hostelId}")
    public ResponseEntity<List<InvoiceDto>> getHostelInvoices(@PathVariable UUID hostelId) {
        return ResponseEntity.ok(billingUseCase.getHostelInvoices(hostelId));
    }

    // ─── Payments ──────────────────────────────────────────────────────────────

    @PostMapping("/payments")
    public ResponseEntity<PaymentDto> recordPayment(@Valid @RequestBody RecordPaymentRequest request) {
        PaymentDto payment = billingUseCase.recordPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentDto>> getInvoicePayments(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingUseCase.getPaymentsForInvoice(invoiceId));
    }

    // ─── Stats & Operations ────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<BillingStatsDto> getBillingStats() {
        return ResponseEntity.ok(billingUseCase.getBillingStats());
    }

    @PostMapping("/process-overdue")
    public ResponseEntity<Void> processOverdue() {
        billingUseCase.processOverdueInvoices();
        return ResponseEntity.ok().build();
    }
}

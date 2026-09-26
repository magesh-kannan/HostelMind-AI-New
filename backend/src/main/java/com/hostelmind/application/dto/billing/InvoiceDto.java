package com.hostelmind.application.dto.billing;

import com.hostelmind.domain.model.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private UUID id;
    private String invoiceNumber;
    private UUID studentId;
    private String studentName;
    private UUID hostelId;
    private UUID roomId;
    private String academicYear;
    private String billingPeriod;
    private BigDecimal subtotal;
    private BigDecimal lateFee;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingBalance;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

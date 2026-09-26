package com.hostelmind.domain.model;

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
public class Invoice {
    private UUID id;
    private String invoiceNumber;
    private UUID studentId;
    private UUID hostelId;
    private UUID roomId;
    private String academicYear;
    private String billingPeriod;
    private BigDecimal subtotal;
    private BigDecimal lateFee;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public void applyPayment(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;
        
        this.paidAmount = this.paidAmount.add(amount);
        
        if (this.paidAmount.compareTo(this.totalAmount) >= 0) {
            this.status = InvoiceStatus.PAID;
        } else {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }
        this.updatedAt = Instant.now();
    }

    public void recalculateTotal() {
        BigDecimal sub = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal late = lateFee != null ? lateFee : BigDecimal.ZERO;
        this.totalAmount = sub.add(late);
    }

    public BigDecimal getRemainingBalance() {
        BigDecimal total = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        return total.subtract(paid).max(BigDecimal.ZERO);
    }
}

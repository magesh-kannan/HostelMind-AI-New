package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructure {
    private UUID id;
    private UUID hostelId;
    private String roomType;
    private String academicYear;
    private BigDecimal rentAmount;
    private BigDecimal utilityDeposit;
    private BigDecimal messFee;
    private BigDecimal otherCharges;
    private Integer dueDayOfMonth;
    private BigDecimal lateFeePerDay;
    private Instant createdAt;
    private Instant updatedAt;

    public BigDecimal getTotalFee() {
        BigDecimal total = BigDecimal.ZERO;
        if (rentAmount != null) total = total.add(rentAmount);
        if (utilityDeposit != null) total = total.add(utilityDeposit);
        if (messFee != null) total = total.add(messFee);
        if (otherCharges != null) total = total.add(otherCharges);
        return total;
    }
}

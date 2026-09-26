package com.hostelmind.application.dto.billing;

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
public class FeeStructureDto {
    private UUID id;
    private UUID hostelId;
    private String roomType;
    private String academicYear;
    private BigDecimal rentAmount;
    private BigDecimal utilityDeposit;
    private BigDecimal messFee;
    private BigDecimal otherCharges;
    private BigDecimal totalFee;
    private Integer dueDayOfMonth;
    private BigDecimal lateFeePerDay;
    private Instant createdAt;
    private Instant updatedAt;
}

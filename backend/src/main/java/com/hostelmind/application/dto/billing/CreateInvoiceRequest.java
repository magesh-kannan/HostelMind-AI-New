package com.hostelmind.application.dto.billing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    @NotNull(message = "Student ID is required")
    private UUID studentId;

    private UUID hostelId;
    private UUID roomId;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotBlank(message = "Billing period is required")
    private String billingPeriod;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotNull
    private BigDecimal rentAmount;
    private BigDecimal utilityDeposit = BigDecimal.ZERO;
    private BigDecimal messFee = BigDecimal.ZERO;
    private BigDecimal otherCharges = BigDecimal.ZERO;
}

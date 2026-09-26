package com.hostelmind.application.dto.billing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeStructureRequest {
    @NotNull(message = "Hostel ID is required")
    private UUID hostelId;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull
    @PositiveOrZero
    private BigDecimal rentAmount;

    @PositiveOrZero
    private BigDecimal utilityDeposit = BigDecimal.ZERO;

    @PositiveOrZero
    private BigDecimal messFee = BigDecimal.ZERO;

    @PositiveOrZero
    private BigDecimal otherCharges = BigDecimal.ZERO;

    private Integer dueDayOfMonth = 5;

    @PositiveOrZero
    private BigDecimal lateFeePerDay = BigDecimal.valueOf(50.00);
}

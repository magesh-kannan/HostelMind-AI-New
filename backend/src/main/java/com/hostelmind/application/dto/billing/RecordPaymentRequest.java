package com.hostelmind.application.dto.billing;

import com.hostelmind.domain.model.PaymentGatewayProvider;
import com.hostelmind.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordPaymentRequest {
    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    private PaymentGatewayProvider gatewayProvider = PaymentGatewayProvider.STRIPE;
    private String transactionReference;
}

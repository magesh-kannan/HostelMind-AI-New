package com.hostelmind.application.dto.billing;

import com.hostelmind.domain.model.PaymentGatewayProvider;
import com.hostelmind.domain.model.PaymentMethod;
import com.hostelmind.domain.model.PaymentStatus;
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
public class PaymentDto {
    private UUID id;
    private UUID invoiceId;
    private UUID studentId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private PaymentGatewayProvider gatewayProvider;
    private PaymentStatus status;
    private Instant paidAt;
}

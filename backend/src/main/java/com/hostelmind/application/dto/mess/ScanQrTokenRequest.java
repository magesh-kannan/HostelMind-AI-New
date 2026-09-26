package com.hostelmind.application.dto.mess;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanQrTokenRequest {
    @NotBlank(message = "QR Token is required")
    private String qrToken;
}

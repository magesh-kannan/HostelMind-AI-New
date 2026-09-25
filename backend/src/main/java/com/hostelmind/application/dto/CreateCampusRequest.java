package com.hostelmind.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampusRequest {
    @NotBlank(message = "Campus name is required")
    private String name;

    @NotBlank(message = "Campus code is required")
    private String code;

    private String address;
}

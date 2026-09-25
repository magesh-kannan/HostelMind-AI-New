package com.hostelmind.application.dto;

import com.hostelmind.domain.model.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHostelRequest {
    @NotNull(message = "Campus ID is required")
    private UUID campusId;

    @NotBlank(message = "Hostel name is required")
    private String name;

    @NotNull(message = "Gender type is required")
    private GenderType genderType;

    private UUID wardenId;
}

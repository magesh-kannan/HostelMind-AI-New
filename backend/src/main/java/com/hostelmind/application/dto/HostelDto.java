package com.hostelmind.application.dto;

import com.hostelmind.domain.model.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelDto {
    private UUID id;
    private UUID campusId;
    private String name;
    private GenderType genderType;
    private UUID wardenId;
}

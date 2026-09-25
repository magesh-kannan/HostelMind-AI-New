package com.hostelmind.application.dto;

import com.hostelmind.domain.model.BedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedDto {
    private UUID id;
    private UUID roomId;
    private String bedNumber;
    private BedStatus status;
}

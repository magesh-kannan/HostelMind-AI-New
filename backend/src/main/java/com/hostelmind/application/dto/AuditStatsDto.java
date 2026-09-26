package com.hostelmind.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditStatsDto {
    private int totalEvents;
    private Map<String, Long> byEntityType;
    private Map<String, Long> byAction;
}

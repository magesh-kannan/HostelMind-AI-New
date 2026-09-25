package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentRun {
    private UUID id;
    private String agentName;
    private String inputSummary;
    private String outputSummary;
    private Double confidenceScore;
    private String modelUsed;
    private Long executionTimeMs;
    private boolean requiresHumanApproval;
    private Boolean humanApproved;
    private String finalDecision;
    private Instant createdAt;
}

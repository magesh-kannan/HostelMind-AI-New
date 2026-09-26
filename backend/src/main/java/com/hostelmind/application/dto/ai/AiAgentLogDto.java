package com.hostelmind.application.dto.ai;

import com.hostelmind.domain.model.AgentType;
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
public class AiAgentLogDto {
    private UUID id;
    private AgentType agentType;
    private String prompt;
    private String response;
    private String modelName;
    private Integer tokensUsed;
    private BigDecimal confidenceScore;
    private UUID performedBy;
    private Instant createdAt;
}

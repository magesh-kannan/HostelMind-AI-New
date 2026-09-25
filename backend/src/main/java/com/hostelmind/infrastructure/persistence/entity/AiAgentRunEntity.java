package com.hostelmind.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_agent_runs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "agent_name", nullable = false, length = 100)
    private String agentName;

    @Column(name = "input_summary", columnDefinition = "TEXT")
    private String inputSummary;

    @Column(name = "output_summary", columnDefinition = "TEXT")
    private String outputSummary;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "model_used", length = 100)
    private String modelUsed;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "requires_human_approval", nullable = false)
    private boolean requiresHumanApproval;

    @Column(name = "human_approved")
    private Boolean humanApproved;

    @Column(name = "final_decision", columnDefinition = "TEXT")
    private String finalDecision;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

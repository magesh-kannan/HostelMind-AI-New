package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AiAgentRun;
import com.hostelmind.domain.port.AiAgentRunRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.AiAgentRunEntity;
import com.hostelmind.infrastructure.persistence.repository.SpringDataAiAgentRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiAgentRunRepositoryAdapter implements AiAgentRunRepositoryPort {

    private final SpringDataAiAgentRunRepository springDataAiAgentRunRepository;

    @Override
    public AiAgentRun save(AiAgentRun aiAgentRun) {
        AiAgentRunEntity entity = toEntity(aiAgentRun);
        AiAgentRunEntity saved = springDataAiAgentRunRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AiAgentRun> findById(UUID id) {
        return springDataAiAgentRunRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AiAgentRun> findByAgentName(String agentName) {
        return springDataAiAgentRunRepository.findByAgentName(agentName)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AiAgentRunEntity toEntity(AiAgentRun run) {
        return AiAgentRunEntity.builder()
                .id(run.getId())
                .agentName(run.getAgentName())
                .inputSummary(run.getInputSummary())
                .outputSummary(run.getOutputSummary())
                .confidenceScore(run.getConfidenceScore())
                .modelUsed(run.getModelUsed())
                .executionTimeMs(run.getExecutionTimeMs())
                .requiresHumanApproval(run.isRequiresHumanApproval())
                .humanApproved(run.getHumanApproved())
                .finalDecision(run.getFinalDecision())
                .createdAt(run.getCreatedAt())
                .build();
    }

    private AiAgentRun toDomain(AiAgentRunEntity entity) {
        return AiAgentRun.builder()
                .id(entity.getId())
                .agentName(entity.getAgentName())
                .inputSummary(entity.getInputSummary())
                .outputSummary(entity.getOutputSummary())
                .confidenceScore(entity.getConfidenceScore())
                .modelUsed(entity.getModelUsed())
                .executionTimeMs(entity.getExecutionTimeMs())
                .requiresHumanApproval(entity.isRequiresHumanApproval())
                .humanApproved(entity.getHumanApproved())
                .finalDecision(entity.getFinalDecision())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

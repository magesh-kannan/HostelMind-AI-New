package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.AgentType;
import com.hostelmind.domain.model.AiAgentLog;
import com.hostelmind.domain.repository.AiAgentLogRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.AiAgentLogEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaAiAgentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiAgentLogRepositoryAdapter implements AiAgentLogRepositoryPort {

    private final JpaAiAgentLogRepository jpaRepository;

    @Override
    public AiAgentLog save(AiAgentLog log) {
        AiAgentLogEntity entity = toEntity(log);
        AiAgentLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AiAgentLog> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<AiAgentLog> findByAgentType(AgentType agentType) {
        return jpaRepository.findByAgentType(agentType).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AiAgentLog> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private AiAgentLogEntity toEntity(AiAgentLog domain) {
        return AiAgentLogEntity.builder()
                .id(domain.getId())
                .agentType(domain.getAgentType())
                .prompt(domain.getPrompt())
                .response(domain.getResponse())
                .modelName(domain.getModelName())
                .tokensUsed(domain.getTokensUsed())
                .confidenceScore(domain.getConfidenceScore())
                .performedBy(domain.getPerformedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private AiAgentLog toDomain(AiAgentLogEntity entity) {
        return AiAgentLog.builder()
                .id(entity.getId())
                .agentType(entity.getAgentType())
                .prompt(entity.getPrompt())
                .response(entity.getResponse())
                .modelName(entity.getModelName())
                .tokensUsed(entity.getTokensUsed())
                .confidenceScore(entity.getConfidenceScore())
                .performedBy(entity.getPerformedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

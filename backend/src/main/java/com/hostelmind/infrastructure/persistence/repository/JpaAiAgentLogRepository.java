package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.AgentType;
import com.hostelmind.infrastructure.persistence.entity.AiAgentLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaAiAgentLogRepository extends JpaRepository<AiAgentLogEntity, UUID> {
    List<AiAgentLogEntity> findByAgentType(AgentType agentType);
}

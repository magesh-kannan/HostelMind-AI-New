package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.AgentType;
import com.hostelmind.domain.model.AiAgentLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiAgentLogRepositoryPort {
    AiAgentLog save(AiAgentLog log);
    Optional<AiAgentLog> findById(UUID id);
    List<AiAgentLog> findByAgentType(AgentType agentType);
    List<AiAgentLog> findAll();
}

package com.hostelmind.domain.port;

import com.hostelmind.domain.model.AiAgentRun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiAgentRunRepositoryPort {
    AiAgentRun save(AiAgentRun aiAgentRun);
    Optional<AiAgentRun> findById(UUID id);
    List<AiAgentRun> findByAgentName(String agentName);
}

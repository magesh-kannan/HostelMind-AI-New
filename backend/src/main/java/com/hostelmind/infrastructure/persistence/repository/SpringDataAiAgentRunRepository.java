package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.AiAgentRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiAgentRunRepository extends JpaRepository<AiAgentRunEntity, UUID> {
    List<AiAgentRunEntity> findByAgentName(String agentName);
}

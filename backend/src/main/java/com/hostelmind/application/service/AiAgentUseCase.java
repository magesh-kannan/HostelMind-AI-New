package com.hostelmind.application.service;

import com.hostelmind.application.dto.ai.*;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.repository.AiAgentLogRepositoryPort;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.KnowledgeDocumentRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAgentUseCase {

    private final KnowledgeDocumentRepositoryPort knowledgeRepository;
    private final AiAgentLogRepositoryPort aiLogRepository;
    private final AuditLogRepositoryPort auditLogRepository;

    private static final String MODEL_NAME = "HostelMind-RAG-v1.0";

    // ─── Complaint AI Classification ───────────────────────────────────────────

    @Transactional
    public ClassifyComplaintAiResponse classifyComplaint(UUID userId, ClassifyComplaintAiRequest request) {
        String text = (request.getTitle() + " " + request.getDescription()).toLowerCase();

        ComplaintCategory category = determineCategory(text);
        ComplaintPriority priority = determinePriority(category, text);
        int slaHours = determineSlaHours(priority);
        double confidence = calculateConfidence(text);

        String reasoning = String.format(
            "Analyzed title and description. Matched category '%s' with priority '%s' based on operational rules and urgency keywords.",
            category, priority
        );

        // Record AI Agent Provenance Log
        AiAgentLog aiLog = AiAgentLog.builder()
                .agentType(AgentType.COMPLAINT_CLASSIFIER)
                .prompt(String.format("Title: %s | Description: %s", request.getTitle(), request.getDescription()))
                .response(String.format("Category: %s | Priority: %s | SLA: %dh", category, priority, slaHours))
                .modelName(MODEL_NAME)
                .tokensUsed(text.length() / 4 + 50)
                .confidenceScore(BigDecimal.valueOf(confidence))
                .performedBy(userId)
                .createdAt(Instant.now())
                .build();
        aiLogRepository.save(aiLog);

        // Operational Audit Log
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .action("AI_CLASSIFY_COMPLAINT")
                .entityType("AiAgent")
                .details(String.format("Classified complaint to category=%s, priority=%s", category, priority))
                .createdAt(Instant.now())
                .build());

        return ClassifyComplaintAiResponse.builder()
                .recommendedCategory(category)
                .recommendedPriority(priority)
                .confidenceScore(BigDecimal.valueOf(confidence))
                .reasoning(reasoning)
                .suggestedSlaHours(slaHours)
                .build();
    }

    // ─── RAG Smart Assistant ───────────────────────────────────────────────────

    @Transactional
    public AskAiResponse askAssistant(UUID userId, AskAiRequest request) {
        String query = request.getQuery();
        
        // 1. Vector / Keyword Context Retrieval
        List<KnowledgeDocument> matchingDocs;
        if (request.getCategory() != null) {
            matchingDocs = knowledgeRepository.findByCategory(request.getCategory());
        } else {
            matchingDocs = knowledgeRepository.searchByKeyword(query);
        }

        if (matchingDocs.isEmpty()) {
            matchingDocs = knowledgeRepository.findAll();
        }

        // 2. Select top relevant context snippets
        List<AskAiResponse.CitedDocumentDto> citations = matchingDocs.stream()
                .limit(3)
                .map(doc -> AskAiResponse.CitedDocumentDto.builder()
                        .id(doc.getId() != null ? doc.getId().toString() : UUID.randomUUID().toString())
                        .title(doc.getTitle())
                        .category(doc.getCategory().name())
                        .snippet(doc.getContent().length() > 150 ? doc.getContent().substring(0, 150) + "..." : doc.getContent())
                        .build())
                .collect(Collectors.toList());

        // 3. Construct Answer
        String answer;
        if (!citations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Based on the official Hostel Rules & Regulations:\n\n");
            for (AskAiResponse.CitedDocumentDto cite : citations) {
                sb.append("• ").append(cite.getTitle()).append(": ").append(cite.getSnippet()).append("\n");
            }
            sb.append("\nFor further assistance, please contact the hostel warden or submit a formal complaint ticket.");
            answer = sb.toString();
        } else {
            answer = "I couldn't find specific institutional documentation regarding your query. Please contact hostel management directly or submit a support complaint.";
        }

        int tokensUsed = query.length() / 4 + answer.length() / 4 + 80;
        BigDecimal confidence = citations.isEmpty() ? BigDecimal.valueOf(0.5000) : BigDecimal.valueOf(0.9200);

        // Record AI Agent Log
        AiAgentLog aiLog = AiAgentLog.builder()
                .agentType(AgentType.RAG_ASSISTANT)
                .prompt(query)
                .response(answer)
                .modelName(MODEL_NAME)
                .tokensUsed(tokensUsed)
                .confidenceScore(confidence)
                .performedBy(userId)
                .createdAt(Instant.now())
                .build();
        aiLogRepository.save(aiLog);

        return AskAiResponse.builder()
                .answer(answer)
                .citedDocuments(citations)
                .confidenceScore(confidence)
                .tokensUsed(tokensUsed)
                .modelName(MODEL_NAME)
                .build();
    }

    // ─── Knowledge Base CRUD ───────────────────────────────────────────────────

    @Transactional
    public KnowledgeDocumentDto createKnowledgeDocument(CreateKnowledgeDocumentRequest req) {
        KnowledgeDocument doc = KnowledgeDocument.builder()
                .title(req.getTitle())
                .category(req.getCategory())
                .content(req.getContent())
                .metadataJson(req.getMetadataJson())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        KnowledgeDocument saved = knowledgeRepository.save(doc);
        return mapToDocumentDto(saved);
    }

    public List<KnowledgeDocumentDto> getAllDocuments() {
        return knowledgeRepository.findAll().stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }

    public List<KnowledgeDocumentDto> getDocumentsByCategory(DocumentCategory category) {
        return knowledgeRepository.findByCategory(category).stream()
                .map(this::mapToDocumentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteKnowledgeDocument(UUID id) {
        knowledgeRepository.deleteById(id);
    }

    // ─── Audit Log Retrieval ───────────────────────────────────────────────────

    public List<AiAgentLogDto> getAgentLogs() {
        return aiLogRepository.findAll().stream()
                .map(this::mapToLogDto)
                .collect(Collectors.toList());
    }

    // ─── Helper Heuristics ─────────────────────────────────────────────────────

    private ComplaintCategory determineCategory(String text) {
        if (text.contains("water") || text.contains("tap") || text.contains("pipe") || text.contains("leak") || text.contains("flush")) {
            return ComplaintCategory.PLUMBING;
        } else if (text.contains("light") || text.contains("fan") || text.contains("power") || text.contains("socket") || text.contains("switch")) {
            return ComplaintCategory.ELECTRICAL;
        } else if (text.contains("wifi") || text.contains("internet") || text.contains("network") || text.contains("router")) {
            return ComplaintCategory.INTERNET_CONNECTIVITY;
        } else if (text.contains("bed") || text.contains("table") || text.contains("chair") || text.contains("cupboard") || text.contains("door")) {
            return ComplaintCategory.FURNITURE;
        } else if (text.contains("food") || text.contains("mess") || text.contains("meal") || text.contains("taste") || text.contains("canteen")) {
            return ComplaintCategory.FOOD_QUALITY;
        } else if (text.contains("clean") || text.contains("dust") || text.contains("garbage") || text.contains("washroom")) {
            return ComplaintCategory.HOUSEKEEPING;
        } else if (text.contains("ac") || text.contains("cooling") || text.contains("air conditioner")) {
            return ComplaintCategory.AC_COOLING;
        } else if (text.contains("rat") || text.contains("bug") || text.contains("cockroach") || text.contains("mosquito")) {
            return ComplaintCategory.PEST_CONTROL;
        } else if (text.contains("security") || text.contains("theft") || text.contains("stranger") || text.contains("lock")) {
            return ComplaintCategory.SECURITY;
        }
        return ComplaintCategory.OTHER;
    }

    private ComplaintPriority determinePriority(ComplaintCategory category, String text) {
        if (text.contains("urgent") || text.contains("emergency") || text.contains("fire") || text.contains("danger") || text.contains("broken lock")) {
            return ComplaintPriority.CRITICAL;
        }
        return switch (category) {
            case SECURITY, MEDICAL -> ComplaintPriority.CRITICAL;
            case WATER_SUPPLY, ELECTRICAL -> ComplaintPriority.HIGH;
            case PLUMBING, AC_COOLING, PEST_CONTROL -> ComplaintPriority.MEDIUM;
            default -> ComplaintPriority.LOW;
        };
    }

    private int determineSlaHours(ComplaintPriority priority) {
        return switch (priority) {
            case CRITICAL -> 4;
            case URGENT -> 12;
            case HIGH -> 24;
            case MEDIUM -> 48;
            case LOW -> 72;
        };
    }

    private double calculateConfidence(String text) {
        if (text.length() > 50) return 0.9500;
        if (text.length() > 20) return 0.8800;
        return 0.7500;
    }

    private KnowledgeDocumentDto mapToDocumentDto(KnowledgeDocument doc) {
        return KnowledgeDocumentDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .category(doc.getCategory())
                .content(doc.getContent())
                .metadataJson(doc.getMetadataJson())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private AiAgentLogDto mapToLogDto(AiAgentLog log) {
        return AiAgentLogDto.builder()
                .id(log.getId())
                .agentType(log.getAgentType())
                .prompt(log.getPrompt())
                .response(log.getResponse())
                .modelName(log.getModelName())
                .tokensUsed(log.getTokensUsed())
                .confidenceScore(log.getConfidenceScore())
                .performedBy(log.getPerformedBy())
                .createdAt(log.getCreatedAt())
                .build();
    }
}

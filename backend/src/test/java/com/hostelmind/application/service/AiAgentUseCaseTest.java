package com.hostelmind.application.service;

import com.hostelmind.application.dto.ai.AskAiRequest;
import com.hostelmind.application.dto.ai.ClassifyComplaintAiRequest;
import com.hostelmind.application.dto.ai.CreateKnowledgeDocumentRequest;
import com.hostelmind.domain.model.*;
import com.hostelmind.domain.repository.AiAgentLogRepositoryPort;
import com.hostelmind.domain.port.AuditLogRepositoryPort;
import com.hostelmind.domain.repository.KnowledgeDocumentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAgentUseCaseTest {

    @Mock
    private KnowledgeDocumentRepositoryPort knowledgeRepository;

    @Mock
    private AiAgentLogRepositoryPort aiLogRepository;

    @Mock
    private AuditLogRepositoryPort auditLogRepository;

    @InjectMocks
    private AiAgentUseCase aiAgentUseCase;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should classify plumbing issue correctly and record AI provenance log")
    void classifyComplaint_Plumbing() {
        ClassifyComplaintAiRequest req = new ClassifyComplaintAiRequest("Leaking pipe", "Water is overflowing in room 102 tap");

        when(aiLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = aiAgentUseCase.classifyComplaint(userId, req);

        assertNotNull(response);
        assertEquals(ComplaintCategory.PLUMBING, response.getRecommendedCategory());
        assertEquals(ComplaintPriority.MEDIUM, response.getRecommendedPriority());
        assertEquals(48, response.getSuggestedSlaHours());
        assertNotNull(response.getReasoning());

        verify(aiLogRepository).save(argThat(log ->
            log.getAgentType() == AgentType.COMPLAINT_CLASSIFIER &&
            log.getPerformedBy().equals(userId)
        ));
    }

    @Test
    @DisplayName("Should classify urgent electrical issue with high priority")
    void classifyComplaint_ElectricalUrgent() {
        ClassifyComplaintAiRequest req = new ClassifyComplaintAiRequest("Sparking power socket", "Urgent! Fire danger near desk");

        when(aiLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = aiAgentUseCase.classifyComplaint(userId, req);

        assertNotNull(response);
        assertEquals(ComplaintCategory.ELECTRICAL, response.getRecommendedCategory());
        assertEquals(ComplaintPriority.CRITICAL, response.getRecommendedPriority());
        assertEquals(4, response.getSuggestedSlaHours());
    }

    @Test
    @DisplayName("Should retrieve matching knowledge base snippets and cite documents in RAG assistant")
    void askAssistant_WithCitations() {
        KnowledgeDocument doc = KnowledgeDocument.builder()
                .id(UUID.randomUUID())
                .title("Hostel Night Curfew Policy")
                .category(DocumentCategory.RULES)
                .content("Students must enter the hostel gate before 10:00 PM every night.")
                .build();

        when(knowledgeRepository.searchByKeyword(any())).thenReturn(List.of(doc));
        when(aiLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AskAiRequest req = new AskAiRequest("What is the curfew time?", null);

        var response = aiAgentUseCase.askAssistant(userId, req);

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("Hostel Night Curfew Policy"));
        assertEquals(1, response.getCitedDocuments().size());
        assertEquals("Hostel Night Curfew Policy", response.getCitedDocuments().get(0).getTitle());

        verify(aiLogRepository).save(argThat(log ->
            log.getAgentType() == AgentType.RAG_ASSISTANT
        ));
    }

    @Test
    @DisplayName("Should handle knowledge base document ingestion")
    void createKnowledgeDocument_Success() {
        CreateKnowledgeDocumentRequest req = new CreateKnowledgeDocumentRequest(
                "Mess Timings",
                DocumentCategory.MESS_MENU,
                "Breakfast 7:30 AM to 9:30 AM, Dinner 7:30 PM to 9:30 PM",
                "{\"season\": \"Fall 2026\"}"
        );

        when(knowledgeRepository.save(any())).thenAnswer(inv -> {
            KnowledgeDocument d = inv.getArgument(0);
            if (d.getId() == null) d.setId(UUID.randomUUID());
            return d;
        });

        var result = aiAgentUseCase.createKnowledgeDocument(req);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Mess Timings", result.getTitle());
        assertEquals(DocumentCategory.MESS_MENU, result.getCategory());
    }
}

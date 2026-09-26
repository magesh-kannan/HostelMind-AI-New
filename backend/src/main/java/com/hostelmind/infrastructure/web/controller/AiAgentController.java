package com.hostelmind.infrastructure.web.controller;

import com.hostelmind.application.dto.ai.*;
import com.hostelmind.application.service.AiAgentUseCase;
import com.hostelmind.domain.model.DocumentCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentUseCase aiAgentUseCase;

    @PostMapping("/classify")
    public ResponseEntity<ClassifyComplaintAiResponse> classifyComplaint(
            Authentication authentication,
            @Valid @RequestBody ClassifyComplaintAiRequest request
    ) {
        UUID userId = extractUserId(authentication);
        ClassifyComplaintAiResponse response = aiAgentUseCase.classifyComplaint(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask")
    public ResponseEntity<AskAiResponse> askAssistant(
            Authentication authentication,
            @Valid @RequestBody AskAiRequest request
    ) {
        UUID userId = extractUserId(authentication);
        AskAiResponse response = aiAgentUseCase.askAssistant(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/knowledge")
    public ResponseEntity<List<KnowledgeDocumentDto>> getKnowledgeDocuments(
            @RequestParam(required = false) DocumentCategory category
    ) {
        if (category != null) {
            return ResponseEntity.ok(aiAgentUseCase.getDocumentsByCategory(category));
        }
        return ResponseEntity.ok(aiAgentUseCase.getAllDocuments());
    }

    @PostMapping("/knowledge")
    public ResponseEntity<KnowledgeDocumentDto> createKnowledgeDocument(
            @Valid @RequestBody CreateKnowledgeDocumentRequest request
    ) {
        KnowledgeDocumentDto created = aiAgentUseCase.createKnowledgeDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/knowledge/{id}")
    public ResponseEntity<Void> deleteKnowledgeDocument(@PathVariable UUID id) {
        aiAgentUseCase.deleteKnowledgeDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<List<AiAgentLogDto>> getAgentLogs() {
        return ResponseEntity.ok(aiAgentUseCase.getAgentLogs());
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }
}

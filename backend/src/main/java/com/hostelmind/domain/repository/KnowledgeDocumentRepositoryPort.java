package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.DocumentCategory;
import com.hostelmind.domain.model.KnowledgeDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeDocumentRepositoryPort {
    KnowledgeDocument save(KnowledgeDocument document);
    Optional<KnowledgeDocument> findById(UUID id);
    List<KnowledgeDocument> findAll();
    List<KnowledgeDocument> findByCategory(DocumentCategory category);
    List<KnowledgeDocument> searchByKeyword(String keyword);
    void deleteById(UUID id);
}

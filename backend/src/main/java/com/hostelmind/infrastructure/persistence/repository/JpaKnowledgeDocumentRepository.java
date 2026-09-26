package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.domain.model.DocumentCategory;
import com.hostelmind.infrastructure.persistence.entity.KnowledgeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaKnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, UUID> {
    List<KnowledgeDocumentEntity> findByCategory(DocumentCategory category);

    @Query("SELECT k FROM KnowledgeDocumentEntity k WHERE LOWER(k.title) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(k.content) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<KnowledgeDocumentEntity> searchByKeyword(String kw);
}

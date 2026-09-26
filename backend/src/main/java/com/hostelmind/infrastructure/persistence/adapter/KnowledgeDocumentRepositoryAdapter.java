package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.DocumentCategory;
import com.hostelmind.domain.model.KnowledgeDocument;
import com.hostelmind.domain.repository.KnowledgeDocumentRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.KnowledgeDocumentEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaKnowledgeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KnowledgeDocumentRepositoryAdapter implements KnowledgeDocumentRepositoryPort {

    private final JpaKnowledgeDocumentRepository jpaRepository;

    @Override
    public KnowledgeDocument save(KnowledgeDocument doc) {
        KnowledgeDocumentEntity entity = toEntity(doc);
        KnowledgeDocumentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<KnowledgeDocument> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<KnowledgeDocument> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> findByCategory(DocumentCategory category) {
        return jpaRepository.findByCategory(category).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> searchByKeyword(String keyword) {
        return jpaRepository.searchByKeyword(keyword).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private KnowledgeDocumentEntity toEntity(KnowledgeDocument domain) {
        return KnowledgeDocumentEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .category(domain.getCategory())
                .content(domain.getContent())
                .metadataJson(domain.getMetadataJson())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private KnowledgeDocument toDomain(KnowledgeDocumentEntity entity) {
        return KnowledgeDocument.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .category(entity.getCategory())
                .content(entity.getContent())
                .metadataJson(entity.getMetadataJson())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package com.hostelmind.infrastructure.persistence.adapter;

import com.hostelmind.domain.model.MessFeedback;
import com.hostelmind.domain.repository.MessFeedbackRepositoryPort;
import com.hostelmind.infrastructure.persistence.entity.MessFeedbackEntity;
import com.hostelmind.infrastructure.persistence.repository.JpaMessFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessFeedbackRepositoryAdapter implements MessFeedbackRepositoryPort {

    private final JpaMessFeedbackRepository jpaRepository;

    @Override
    public MessFeedback save(MessFeedback domain) {
        MessFeedbackEntity entity = toEntity(domain);
        MessFeedbackEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MessFeedback> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MessFeedback> findByStudentId(UUID studentId) {
        return jpaRepository.findByStudentId(studentId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MessFeedback> findByMenuId(UUID menuId) {
        return jpaRepository.findByMenuId(menuId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MessFeedback> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private MessFeedbackEntity toEntity(MessFeedback domain) {
        return MessFeedbackEntity.builder()
                .id(domain.getId())
                .studentId(domain.getStudentId())
                .menuId(domain.getMenuId())
                .rating(domain.getRating())
                .comment(domain.getComment())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private MessFeedback toDomain(MessFeedbackEntity entity) {
        return MessFeedback.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .menuId(entity.getMenuId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

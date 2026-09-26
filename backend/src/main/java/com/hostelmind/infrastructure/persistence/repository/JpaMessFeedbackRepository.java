package com.hostelmind.infrastructure.persistence.repository;

import com.hostelmind.infrastructure.persistence.entity.MessFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaMessFeedbackRepository extends JpaRepository<MessFeedbackEntity, UUID> {
    List<MessFeedbackEntity> findByStudentId(UUID studentId);
    List<MessFeedbackEntity> findByMenuId(UUID menuId);
}

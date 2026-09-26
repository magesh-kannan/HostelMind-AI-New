package com.hostelmind.domain.repository;

import com.hostelmind.domain.model.MessFeedback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessFeedbackRepositoryPort {
    MessFeedback save(MessFeedback feedback);
    Optional<MessFeedback> findById(UUID id);
    List<MessFeedback> findByStudentId(UUID studentId);
    List<MessFeedback> findByMenuId(UUID menuId);
    List<MessFeedback> findAll();
}

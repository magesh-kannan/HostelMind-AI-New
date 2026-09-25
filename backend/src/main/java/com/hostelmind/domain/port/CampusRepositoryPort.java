package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Campus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampusRepositoryPort {
    Campus save(Campus campus);
    Optional<Campus> findById(UUID id);
    Optional<Campus> findByCode(String code);
    List<Campus> findAll();
    void deleteById(UUID id);
}

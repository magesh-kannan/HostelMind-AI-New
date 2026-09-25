package com.hostelmind.domain.port;

import com.hostelmind.domain.model.Hostel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HostelRepositoryPort {
    Hostel save(Hostel hostel);
    Optional<Hostel> findById(UUID id);
    List<Hostel> findByCampusId(UUID campusId);
    List<Hostel> findAll();
    long count();
}

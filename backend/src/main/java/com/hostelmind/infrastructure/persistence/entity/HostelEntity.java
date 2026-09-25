package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.GenderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hostels")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campus_id", nullable = false)
    private UUID campusId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_type", nullable = false)
    private GenderType genderType;

    @Column(name = "warden_id")
    private UUID wardenId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

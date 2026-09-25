package com.hostelmind.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "floors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "block_id", nullable = false)
    private UUID blockId;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @Column(length = 100)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

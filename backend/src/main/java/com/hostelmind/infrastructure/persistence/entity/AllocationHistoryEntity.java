package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.AllocationActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "allocation_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "allocation_id", nullable = false)
    private UUID allocationId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private AllocationActionType actionType;

    @Column(name = "from_bed_id")
    private UUID fromBedId;

    @Column(name = "to_bed_id")
    private UUID toBedId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

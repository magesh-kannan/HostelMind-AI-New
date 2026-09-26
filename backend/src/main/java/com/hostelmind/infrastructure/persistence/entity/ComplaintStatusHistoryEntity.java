package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.ComplaintStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaint_status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintStatusHistoryEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "complaint_id", nullable = false)
    private UUID complaintId;

    @Column(name = "changed_by_user_id")
    private UUID changedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private ComplaintStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private ComplaintStatus toStatus;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;
}

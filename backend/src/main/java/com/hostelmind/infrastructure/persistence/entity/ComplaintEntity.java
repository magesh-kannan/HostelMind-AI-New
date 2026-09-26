package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintPriority;
import com.hostelmind.domain.model.ComplaintStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "hostel_id", nullable = false)
    private UUID hostelId;

    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private ComplaintPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(name = "sla_deadline")
    private Instant slaDeadline;

    @Column(name = "reopen_count", nullable = false)
    private int reopenCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;
}

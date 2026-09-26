package com.hostelmind.infrastructure.persistence.entity;

import com.hostelmind.domain.model.DayOfWeek;
import com.hostelmind.domain.model.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mess_menus", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hostel_id", "day_of_week", "meal_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessMenuEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "hostel_id", nullable = false)
    private UUID hostelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String items;

    @Column(name = "calorie_count")
    private Integer calorieCount;

    @Column(name = "special_notes")
    private String specialNotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

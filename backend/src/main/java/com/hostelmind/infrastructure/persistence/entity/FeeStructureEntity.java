package com.hostelmind.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fee_structures", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hostel_id", "room_type", "academic_year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "hostel_id", nullable = false)
    private UUID hostelId;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "rent_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Column(name = "utility_deposit", precision = 10, scale = 2)
    private BigDecimal utilityDeposit;

    @Column(name = "mess_fee", precision = 10, scale = 2)
    private BigDecimal messFee;

    @Column(name = "other_charges", precision = 10, scale = 2)
    private BigDecimal otherCharges;

    @Column(name = "due_day_of_month", nullable = false)
    private Integer dueDayOfMonth;

    @Column(name = "late_fee_per_day", nullable = false, precision = 10, scale = 2)
    private BigDecimal lateFeePerDay;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

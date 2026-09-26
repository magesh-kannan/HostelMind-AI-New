package com.hostelmind.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessFeedback {
    private UUID id;
    private UUID studentId;
    private UUID menuId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}

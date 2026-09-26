package com.hostelmind.application.dto.mess;

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
public class MessFeedbackDto {
    private UUID id;
    private UUID studentId;
    private UUID menuId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}

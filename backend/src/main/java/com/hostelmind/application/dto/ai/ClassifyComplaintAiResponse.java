package com.hostelmind.application.dto.ai;

import com.hostelmind.domain.model.ComplaintCategory;
import com.hostelmind.domain.model.ComplaintPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassifyComplaintAiResponse {
    private ComplaintCategory recommendedCategory;
    private ComplaintPriority recommendedPriority;
    private BigDecimal confidenceScore;
    private String reasoning;
    private Integer suggestedSlaHours;
}

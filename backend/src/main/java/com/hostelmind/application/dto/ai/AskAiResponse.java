package com.hostelmind.application.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AskAiResponse {
    private String answer;
    private List<CitedDocumentDto> citedDocuments;
    private BigDecimal confidenceScore;
    private Integer tokensUsed;
    private String modelName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CitedDocumentDto {
        private String id;
        private String title;
        private String category;
        private String snippet;
    }
}

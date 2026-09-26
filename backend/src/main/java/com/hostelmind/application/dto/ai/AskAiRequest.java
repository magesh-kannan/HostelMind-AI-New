package com.hostelmind.application.dto.ai;

import com.hostelmind.domain.model.DocumentCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskAiRequest {
    @NotBlank(message = "Query is required")
    private String query;

    private DocumentCategory category;
}

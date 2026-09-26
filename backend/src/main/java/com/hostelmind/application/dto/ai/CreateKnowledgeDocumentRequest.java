package com.hostelmind.application.dto.ai;

import com.hostelmind.domain.model.DocumentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateKnowledgeDocumentRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Category is required")
    private DocumentCategory category;

    @NotBlank(message = "Content is required")
    private String content;

    private String metadataJson;
}

package com.hostelmind.infrastructure.ai;

import com.hostelmind.domain.port.AiProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class LangChain4jAdapter implements AiProviderPort {

    @Value("${app.ai.openai-key:}")
    private String apiKey;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    @Override
    public String generateResponse(String systemPrompt, String userPrompt) {
        if (!StringUtils.hasText(apiKey)) {
            log.info("[AI Provider Stub] API Key not set. Simulated response for model: {}", modelName);
            return "{\"status\": \"SIMULATED_AI_RESPONSE\", \"summary\": \"Simulated output for: " 
                    + userPrompt.replaceAll("\"", "'") + "\", \"confidence\": 0.95}";
        }

        try {
            // Real LangChain4j OpenAiChatModel call path when API key is provided
            dev.langchain4j.model.openai.OpenAiChatModel model = dev.langchain4j.model.openai.OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .temperature(0.2)
                    .build();

            return model.generate(systemPrompt + "\n\nUser: " + userPrompt);
        } catch (Exception e) {
            log.error("Error executing LangChain4j call", e);
            return "{\"status\": \"ERROR\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    @Override
    public float[] embedText(String text) {
        // Fallback or real embedding generation vector (length 384 or 1536)
        float[] embedding = new float[384];
        for (int i = 0; i < 384; i++) {
            embedding[i] = (float) Math.sin(text.hashCode() + i);
        }
        return embedding;
    }
}

package com.hostelmind.domain.port;

public interface AiProviderPort {
    String generateResponse(String systemPrompt, String userPrompt);
    float[] embedText(String text);
}

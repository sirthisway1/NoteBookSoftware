package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.service.AudioGenerationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AudioGenerationServiceImpl implements AudioGenerationService {

    @Value("${deepbricks.api.url}")
    private String deepBricksUrl;

    @Value("${deepbricks.api.key}")
    private String apiKey;

    private static final String AUDIO_API_URL = "http://127.0.0.1:9880";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AudioGenerationServiceImpl(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] generateAudio(AudioRequestDTO request) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("refer_wav_path", request.getReferWavPath());
        requestBody.put("prompt_text", request.getPromptText());
        requestBody.put("prompt_language", request.getPromptLanguage());
        requestBody.put("text", request.getText());
        requestBody.put("text_language", request.getTextLanguage());

        return webClient.post()
                .uri(AUDIO_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(byte[].class)
                .timeout(Duration.ofSeconds(30))
                .block();
    }

    public String generateTextResponse(String userInput) throws Exception {
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "gpt-4o");
        requestPayload.put("messages", Collections.singletonList(
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", userInput);
                }}
        ));
        requestPayload.put("stream", true);

        String response = webClient.post()
                .uri(deepBricksUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();

        return extractAnswerFromResponse(response);
    }

    private String extractAnswerFromResponse(String responseBody) throws Exception {
        StringBuilder completeResponse = new StringBuilder();
        String[] chunks = responseBody.split("\n\n");

        for (String chunk : chunks) {
            if (chunk.equals("data: [DONE]")) {
                break;
            }
            if (chunk.startsWith("data: ")) {
                String jsonChunk = chunk.substring(6);
                JsonNode node = objectMapper.readTree(jsonChunk);
                JsonNode delta = node.path("choices").get(0).path("delta").path("content");
                if (!delta.isMissingNode()) {
                    completeResponse.append(delta.asText());
                }
            }
        }

        return completeResponse.toString();
    }
}
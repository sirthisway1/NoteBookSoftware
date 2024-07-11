package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.service.AudioGenerationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.HttpStatus;
import java.util.Collections;




@Service
public class AudioGenerationServiceImpl implements AudioGenerationService {

    @Value("${deepbricks.api.url}")
    private String deepBricksUrl;

    @Value("${deepbricks.api.key}")
    private String apiKey;

    private static final String AUDIO_API_URL = "http://127.0.0.1:9880";
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AudioGenerationServiceImpl.class);
    private final WebClient webClient;

    public AudioGenerationServiceImpl(WebClient webClient) {
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public byte[] generateAudio(AudioRequestDTO request) throws Exception {
        // 创建ExchangeStrategies以调整缓冲区大小
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();

        // 创建WebClient实例，并应用ExchangeStrategies
        WebClient webClient = WebClient.builder()
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .build();

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("refer_wav_path", request.getReferWavPath());
        requestBody.put("prompt_text", request.getPromptText());
        requestBody.put("prompt_language", request.getPromptLanguage());
        requestBody.put("text", request.getText());
        requestBody.put("text_language", request.getTextLanguage());

        logger.info("Sending request to AUDIO_API_URL with requestBody: {}", requestBody);

        byte[] audioBytes = null;
        try {
            audioBytes = webClient.post()
                    .uri(AUDIO_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        logger.error("Error response status: {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error response body: {}", errorBody);
                                    HttpStatus httpStatus = HttpStatus.resolve(clientResponse.statusCode().value());
                                    String reasonPhrase = (httpStatus != null) ? httpStatus.getReasonPhrase() : "Unknown status";
                                    return Mono.error(new WebClientResponseException(
                                            errorBody,
                                            clientResponse.statusCode().value(),
                                            reasonPhrase,
                                            clientResponse.headers().asHttpHeaders(),
                                            null,
                                            null));
                                });
                    })
                    .bodyToMono(byte[].class)
                    .doOnNext(bytes -> logger.info("Received {} bytes", bytes.length))
                    .doOnError(e -> logger.error("Error occurred while generating audio", e))
                    .blockOptional()
                    .orElseThrow(() -> new Exception("Failed to generate audio, response was null"));
        } catch (Exception e) {
            logger.error("Exception during audio generation", e);
            throw e;
        }

        logger.info("Audio generation successful");

        return audioBytes;
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
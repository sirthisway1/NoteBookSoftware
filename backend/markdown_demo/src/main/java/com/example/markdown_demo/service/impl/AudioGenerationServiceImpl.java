package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.service.AudioGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AudioGenerationServiceImpl implements AudioGenerationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${deepbricks.api.url}")
    private String deepBricksUrl;

    @Value("${deepbricks.api.key}")
    private String apiKey;

    private static final String AUDIO_API_URL = "http://127.0.0.1:9880";

    @Override
    public byte[] generateAudio(AudioRequestDTO request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("refer_wav_path", request.getReferWavPath());
        requestBody.put("prompt_text", request.getPromptText());
        requestBody.put("prompt_language", request.getPromptLanguage());
        requestBody.put("text", request.getText());
        requestBody.put("text_language", request.getTextLanguage());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(AUDIO_API_URL, entity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to generate audio: " + new String(response.getBody()));
        }
    }


    public String generateTextResponse(String userInput) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "ChatMindAi-4o");
        requestPayload.put("messages", Collections.singletonList(
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", userInput);
                }}
        ));
        requestPayload.put("stream", true);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(deepBricksUrl, requestEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new Exception("Failed to fetch data from DeepBricks API");
        }
    }
}

package com.example.markdown_demo.service.impl;

import com.example.markdown_demo.service.AudioGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AudioGenerationServiceImpl implements AudioGenerationService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUDIO_API_URL = "http://127.0.0.1:9880";

    @Override
    public byte[] generateAudio(String referWavPath, String promptText, String promptLanguage, String text, String textLanguage) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("refer_wav_path", referWavPath);
        requestBody.put("prompt_text", promptText);
        requestBody.put("prompt_language", promptLanguage);
        requestBody.put("text", text);
        requestBody.put("text_language", textLanguage);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(AUDIO_API_URL, entity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to generate audio: " + new String(response.getBody()));
        }
    }
}

package com.example.markdown_demo.controller;


import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.service.AudioGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/api")
public class AudioAnswerController {

    @Autowired
    private AudioGenerationService audioGenerationService;
    @Autowired
    private RestTemplate restTemplate;

    private final String deepBricksUrl = "https://api.deepbricks.ai/v1/chat/completions";
    private final String apiKey = "sk-e5NKQHTAhRn6bj1qmX4Px7eCbRDnBXMHI9Y6KvZAMneTrBsH";

    @PostMapping(value = "/generate-audio-answer", consumes = "application/json", produces = "audio/wav")
    public void generateAudioAnswer(@RequestBody AudioRequestDTO request, HttpServletResponse response) {
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构造DeepBricks请求体
            Map<String, Object> deepBricksRequest = new HashMap<>();
            deepBricksRequest.put("model", "GPT-4o");
            deepBricksRequest.put("messages", Collections.singletonList(
                    new HashMap<String, String>() {{
                        put("role", "user");
                        put("content", request.getText());
                    }}
            ));
            deepBricksRequest.put("stream", true);

            // 发送请求到DeepBricks API获取回答文本
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(deepBricksRequest, headers);
            ResponseEntity<String> deepBricksResponse = restTemplate.postForEntity(deepBricksUrl, httpEntity, String.class);

            // 使用获取到的回答文本和参考音频路径生成音频
            byte[] audioBytes = audioGenerationService.generateAudio(
                    request.getReferWavPath(),
                    request.getPromptText(),
                    request.getPromptLanguage(),
                    deepBricksResponse.getBody(),
                    request.getTextLanguage());

            // 设置响应内容类型并返回音频流
            response.setContentType("audio/wav");
            response.getOutputStream().write(audioBytes);
            response.flushBuffer();
        } catch (Exception e) {
            // 如果发生错误，返回400状态码和错误信息
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                // 进一步处理错误
            }
        }
    }
}

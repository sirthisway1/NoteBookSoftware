package com.example.markdown_demo.controller;


import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.common.utils.JwtUtil;
import com.example.markdown_demo.service.AudioGenerationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(value = "/generate-text-answer", produces = "application/json")
    public ResponseEntity<String> generateTextAnswer(@RequestParam String text) {
        try {
            String response = audioGenerationService.generateTextResponse(text);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/generate-audio-answer", consumes = "application/json", produces = "audio/wav")
    public void generateAudioAnswer(@RequestBody AudioRequestDTO request, HttpServletResponse response) {
        try {
            byte[] audioBytes = audioGenerationService.generateAudio(request);
            response.setContentType("audio/wav");
            response.getOutputStream().write(audioBytes);
            response.flushBuffer();
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                // 在已经有异常处理时，此处的异常处理是对 IOException 的进一步处理
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }
}

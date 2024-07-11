package com.example.markdown_demo.controller;


import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.service.AudioGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/api")
public class AudioAnswerController {

    @Autowired
    private AudioGenerationService audioGenerationService;

    @GetMapping(value = "/generate-text-answer", produces = "application/json")
    public Result<String> generateTextAnswer(@RequestParam String text) {
        try {
            String response = audioGenerationService.generateTextResponse(text);
            return Result.success(response);
        } catch (Exception e) {
            // Assuming the Result.fail method can take an exception and extract the message
            return Result.fail(e.getMessage(),"错误");
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
            // Since this method is void, we need to handle the error by setting the status and writing the error message
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                // In case of another exception while handling the first one, we set the status to INTERNAL_SERVER_ERROR
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                // Optionally, log the exception or handle it further if needed
            }
        }
    }

}

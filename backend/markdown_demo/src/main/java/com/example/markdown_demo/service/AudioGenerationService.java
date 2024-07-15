package com.example.markdown_demo.service;

import com.example.markdown_demo.common.dto.AudioRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AudioGenerationService {
    byte[] generateAudio(AudioRequestDTO request) throws Exception;
    String generateTextResponse(String userInput)throws Exception;
}

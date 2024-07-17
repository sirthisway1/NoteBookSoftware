package com.example.markdown_demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioToTextService {
    String processAudioFile(MultipartFile audio)throws Exception;

}

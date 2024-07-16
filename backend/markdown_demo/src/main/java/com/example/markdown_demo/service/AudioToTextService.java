package com.example.markdown_demo.service;

import com.example.markdown_demo.common.dto.AudioUploadDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AudioToTextService {
    String processAudioFile(AudioUploadDTO audioUploadDTO)throws Exception;

}

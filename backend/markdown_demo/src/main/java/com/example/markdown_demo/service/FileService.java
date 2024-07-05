package com.example.markdown_demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    Map<String, Object> uploadFile(MultipartFile file);
}

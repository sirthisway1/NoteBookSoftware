package com.example.markdown_demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    Map<String, Object> uploadWangFile(MultipartFile[] files);
    String uploadFile(MultipartFile file);
}

package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

//    @PostMapping("/uploadPicture")
//    public Result<String, Object> uploadPicture(@RequestParam("file") MultipartFile file) {
//        return fileService.uploadFile(file);
//    }
}


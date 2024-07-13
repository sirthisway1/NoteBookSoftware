package com.example.markdown_demo.controller;

import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
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

    @PostMapping("/uploadFile")
    public Result<Object> uploadFile(@RequestParam("file") MultipartFile file) {

        Map<String, Object> uploadResult = fileService.uploadFile(file);
        if (uploadResult != null && Integer.parseInt(uploadResult.get("errno").toString()) == 0) {
            // 成功上传，从uploadResult中获取上传文件的URL
            return Result.success("上传成功", uploadResult);
        } else {
            // 文件上传失败或返回的错误号不为0
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR);
        }



    }

}


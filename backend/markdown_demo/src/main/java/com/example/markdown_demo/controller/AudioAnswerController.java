package com.example.markdown_demo.controller;


import com.example.markdown_demo.common.dto.AudioRequestDTO;
import com.example.markdown_demo.common.dto.AudioUploadDTO;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.service.AudioGenerationService;
import com.example.markdown_demo.service.AudioToTextService;
import com.example.markdown_demo.service.KeyWordService;
import com.example.markdown_demo.service.impl.KeyWordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/model")
public class AudioAnswerController {

    @Autowired
    private AudioGenerationService audioGenerationService;
    @Autowired
    private AudioToTextService audioToTextService;
    @Autowired
    private KeyWordService keyWordService;
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
            response.setStatus(HttpStatus.OK.value());
            try (OutputStream out = response.getOutputStream()) {
                out.write(audioBytes);
                out.flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try {
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                // Optionally, log the exception or handle it further if needed
            }
        }
    }
    @PostMapping(value = "/uploadAudio")
    public Result<?> uploadAudio(@RequestParam("audio") MultipartFile audio){
        // 处理上传的音频文件
        try {
            // 这里调用Service来处理文件，例如保存到服务器或进行语音识别等
            String text=audioToTextService.processAudioFile(audio);
            return Result.success(Map.of("text", text));
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(),"语音转换失败: " + e.getMessage());
        }
    }


    @PostMapping(value = "/keyWord")
    public Result<?> keyWord(@RequestParam("keywords") String text){


        try {

            List<String> keyWords= keyWordService.extractKeyWords(text,5);
            return Result.success(Map.of("keywords", keyWords));
        } catch (Exception e) {
            return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(),"关键字提取失败 " + e.getMessage());
        }



    }
}

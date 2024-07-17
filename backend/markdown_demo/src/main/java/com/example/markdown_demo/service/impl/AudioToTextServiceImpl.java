package com.example.markdown_demo.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.markdown_demo.common.dto.AudioUploadDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.service.AudioToTextService;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.MultimediaInfo;

import java.io.FileInputStream;
import java.util.Base64;
import java.io.File;
import java.io.IOException;

@Service
public class AudioToTextServiceImpl implements AudioToTextService {

    @Value("${baidu.api.key}")
    private String API_KEY;
    @Value("${baidu.api.secret}")
    private String SECRET_KEY;
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final String format = "wav";
    private static String speech = "";
    private static Integer len;
    private static String resultString;
    public String processAudioFile(MultipartFile audio) throws BusinessException {
        try {
            File inputFile = new File(System.getProperty("java.io.tmpdir") + "/" + audio.getOriginalFilename());
            FileUtils.copyInputStreamToFile(audio.getInputStream(), inputFile);
            info(inputFile.getAbsolutePath());
            String outputFilePath = System.getProperty("java.io.tmpdir") + "/converted.wav";
            File outputFile = audioEncode(inputFile.getAbsolutePath(), outputFilePath, 16000, 1);

            convertFileToBase64(outputFile);
            // 删除临时文件
            if (outputFile.exists()) {
                outputFile.delete();
            }
            if (inputFile.exists()) {
                inputFile.delete();
            }
            makeRequest();
            return resultString;
        } catch (IOException e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, "语音转换失败: IO异常");
        } catch (EncoderException e) {
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    public void convertFileToBase64(File file) throws IOException {
        // 将文件转换为字节数组
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = IOUtils.toByteArray(fis);
            // 将字节数组转换为Base64编码的字符串
            speech = Base64.getEncoder().encodeToString(fileBytes);
            // 获取字节数
            len = fileBytes.length;
        }
    }

    private void makeRequest() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyJson = String.format(
                "{\"format\":\"%s\",\"rate\":16000,\"channel\":1,\"cuid\":\"aw6fZ0c9feQMVpZHs0t28jmaktbIuOVq\",\"token\":\"%s\",\"speech\":\"%s\",\"len\":%d,\"dev_pid\":80001}",
                format, getAccessToken(), speech, len
        );
        RequestBody body = RequestBody.create(requestBodyJson, mediaType);
        Request request = new Request.Builder()
                .url("https://vop.baidu.com/pro_api")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String jsonResponse = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode resultNode = rootNode.path("result");
        resultString = resultNode.toString();
        // 去掉中括号和反斜杠
        resultString = resultString.replace("[", "").replace("]", "").replace("\\", "").replace("\"", "");
    }

    private String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

    private void info(String filePath) throws EncoderException {
        File file = new File(filePath);
        MultimediaObject multimediaObject = new MultimediaObject(file);
        MultimediaInfo info = multimediaObject.getInfo();
        AudioInfo audio = info.getAudio();
        int channels = audio.getChannels();
        int bitRate = audio.getBitRate();
        int samplingRate = audio.getSamplingRate();
        System.out.println("声道:" + channels);
        System.out.println("比特率:" + bitRate);
        System.out.println("采样率:" + samplingRate);
    }

    private File audioEncode(String inputFormatPath, String outputFormatPath, int samplingRate, int channels) throws EncoderException {
        String outputFormat = getSuffix(outputFormatPath);
        String inputFormat = getSuffix(inputFormatPath);
        File source = new File(inputFormatPath);
        File target = new File(outputFormatPath);

        MultimediaObject multimediaObject = new MultimediaObject(source);
        MultimediaInfo info = multimediaObject.getInfo();
        AudioInfo audioInfo = info.getAudio();

        AudioAttributes audio = new AudioAttributes();
        audio.setBitRate(audioInfo.getBitRate());
        audio.setSamplingRate(samplingRate);
        audio.setChannels(channels);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setInputFormat(inputFormat);
        attrs.setOutputFormat(outputFormat);
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        encoder.encode(multimediaObject, target, attrs);
        return target;
    }

    private static String getSuffix(String outputFormatPath) {
        return outputFormatPath.substring(outputFormatPath.lastIndexOf(".") + 1);
    }
}
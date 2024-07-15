package com.example.markdown_demo.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.service.AudioToTextService;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import java.util.Base64;
import java.io.IOException;

@Service
public class AudioToTextServiceImpl implements AudioToTextService {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final String API_KEY = "your_api_key_here";
    private static final String SECRET_KEY = "your_secret_key_here";
    private static String speech = "";
    private static String format = "";
    private static Integer len = 0;

    public String processAudioFile(MultipartFile file)throws BusinessException{
        String originalFilename = file.getOriginalFilename();
        format = FileUtil.extName(originalFilename);
        switch (format) {
            case "pcm": case "wav": case "amr": case "m4a":
                try {
                    convertFileToBase64(file);
                    return makeRequest();
                }catch (IOException e){
                    throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "语音转换失败: IO异常");
                }
            default:
                throw new BusinessException(ResultType.INVALID_REQUEST_BODY, "语音转换失败: 文件格式错误");
        }
    }
    private void convertFileToBase64(MultipartFile file) throws IOException {
        // 将文件转换为字节数组
        byte[] fileBytes = IOUtils.toByteArray(file.getInputStream());
        // 将字节数组转换为Base64编码的字符串
        speech = Base64.getEncoder().encodeToString(fileBytes);
        // 获取字节数
        len = fileBytes.length;
    }

    private String makeRequest() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String requestBodyJson = String.format(
                "{\"format\":\"%s\",\"rate\":16000,\"channel\":1,\"cuid\":\"aw6fZ0c9feQMVpZHs0t28jmaktbIuOVq\",\"token\":\"%s\",\"speech\":\"%s\",\"len\":%d,\"dev_pid\":80001}",
                format, getAccessToken(), speech, len
        );
        RequestBody body = RequestBody.create(requestBodyJson, mediaType);Request request = new Request.Builder()
                .url("https://vop.baidu.com/pro_api")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return response.body().string();
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
}

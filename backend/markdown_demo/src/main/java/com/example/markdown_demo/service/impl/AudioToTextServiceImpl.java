package com.example.markdown_demo.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.markdown_demo.common.dto.AudioUploadDTO;
import com.example.markdown_demo.common.lang.BusinessException;
import com.example.markdown_demo.common.lang.Result;
import com.example.markdown_demo.common.lang.ResultType;
import com.example.markdown_demo.service.AudioToTextService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;
import java.util.Base64;
import java.io.IOException;

@Service
public class AudioToTextServiceImpl implements AudioToTextService {

    @Value("${baidu.api.key}")
    private String API_KEY;
    @Value("${baidu.api.secret}")
    private String SECRET_KEY;
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();


    public String processAudioFile(AudioUploadDTO audioUploadDTO)throws BusinessException{

        String filename = audioUploadDTO.getFilename();
        String format = null;
        format = filename.substring(filename.lastIndexOf(".") + 1);
        try {
            return makeRequest(audioUploadDTO.getAudio(),audioUploadDTO.getLen(),format);
        }catch (IOException e){
            throw new BusinessException(ResultType.INTERNAL_SERVER_ERROR.getCode(),e.getMessage());
        }

    }

    private String makeRequest(String speech,Integer len,String format) throws IOException {
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
        String jsonResponse = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode resultNode = rootNode.path("result");
        String resultString = resultNode.toString();
        // 去掉中括号和反斜杠
        resultString = resultString.replace("[", "").replace("]", "").replace("\\", "").replace("\"", "");
        return resultString;
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
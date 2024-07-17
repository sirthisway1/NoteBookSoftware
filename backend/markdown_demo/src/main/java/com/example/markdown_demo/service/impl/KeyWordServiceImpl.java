package com.example.markdown_demo.service.impl;
import com.example.markdown_demo.mapper.NotesMapper;
import com.example.markdown_demo.service.KeyWordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeyWordServiceImpl implements KeyWordService {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    @Value("${keyword.api.key}")
    private String API_KEY;
    @Value("${keyword.api.secret}")
    private String SECRET_KEY;
    @Override
    public List<String> extractKeyWords(String text, int num) throws IOException {
        text = extractTextFromHtml(text);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String json = "{\"text\":[\"" + text + "\"],\"num\":" + num + "}";
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/txt_keywords_extraction?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将JSON字符串转换为JsonNode
            JsonNode jsonNode = objectMapper.readTree(response.body().string());

            // 获取results数组
            JsonNode results = jsonNode.get("results");

            // 创建一个List<String>来存储关键词
            List<String> keywords = new ArrayList<>();

            // 遍历results数组，并提取word字段
            for (JsonNode result : results) {
                String keyword = result.get("word").asText();
                keywords.add(keyword);
            }
            return keywords;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public static String extractTextFromHtml(String htmlContent) {
        // 使用Jsoup解析HTML内容
        Document doc = Jsoup.parse(htmlContent);
        // 提取纯文本
        String text = doc.text();
        return text;
    }





}

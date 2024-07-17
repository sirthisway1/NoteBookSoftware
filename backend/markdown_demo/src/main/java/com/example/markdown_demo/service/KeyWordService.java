package com.example.markdown_demo.service;


import java.io.IOException;
import java.util.List;

public interface KeyWordService {
    /**
     * 提取文本中的关键词。
     *
     * @param text 要提取关键词的文本
     * @param num  提取的关键词数量的最大值
     * @return 关键词提取结果
     * @throws IOException 如果发生IO异常
     */
    List<String> extractKeyWords(String text, int num) throws IOException;
}

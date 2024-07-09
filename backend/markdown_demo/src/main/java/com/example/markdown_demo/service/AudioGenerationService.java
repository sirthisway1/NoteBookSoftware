package com.example.markdown_demo.service;

public interface AudioGenerationService {
    byte[] generateAudio(String referWavPath, String promptText, String promptLanguage, String text, String textLanguage) throws Exception;
}

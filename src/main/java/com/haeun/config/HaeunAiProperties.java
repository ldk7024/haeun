package com.haeun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "haeun.ai")
public class HaeunAiProperties {

    private String provider = "ollama";

    private OllamaProperties ollama = new OllamaProperties();

    @Getter
    @Setter
    public static class OllamaProperties {
        private String baseUrl = "http://localhost:11434";
        private String model = "gemma3:latest";
        private int timeoutSeconds = 60;

        /** UI 드롭다운에 표시할 선택 가능 모델 목록 */
        private List<String> availableModels = new ArrayList<>();
    }
}

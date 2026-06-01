package com.haeun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml 의 haeun.ai.* 설정을 바인딩합니다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "haeun.ai")
public class HaeunAiProperties {

    /** 사용할 AI 제공자 (ollama | mock) */
    private String provider = "ollama";

    private OllamaProperties ollama = new OllamaProperties();

    @Getter
    @Setter
    public static class OllamaProperties {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3.1:8b";
        private int timeoutSeconds = 60;
    }
}

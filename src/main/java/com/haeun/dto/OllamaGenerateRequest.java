package com.haeun.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class OllamaGenerateRequest {

    private String model;
    private String prompt;
    private boolean stream;

    /**
     * Ollama options — 한국어 일관성을 위한 파라미터
     *
     * temperature:    낮을수록 결정적 응답 (0.4 → 언어 혼용 억제)
     * top_p:          어휘 선택 범위 제한 (0.8)
     * repeat_penalty: 반복 표현 패널티 (1.1)
     */
    private Map<String, Object> options;

    public OllamaGenerateRequest(String model, String prompt) {
        this.model  = model;
        this.prompt = prompt;
        this.stream = false;

        this.options = new HashMap<>();
        this.options.put("temperature",    0.4);
        this.options.put("top_p",          0.8);
        this.options.put("repeat_penalty", 1.1);
    }
}

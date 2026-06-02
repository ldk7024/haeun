package com.haeun.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ChatRequest {
    private String message;

    /** 선택한 Ollama 모델명. null이면 application.yml 기본 모델 사용 */
    private String model;
}

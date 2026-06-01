package com.haeun.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OllamaGenerateRequest {

    private String model;
    private String prompt;
    private boolean stream;

    public OllamaGenerateRequest(String model, String prompt) {
        this.model = model;
        this.prompt = prompt;
        this.stream = false;
    }
}

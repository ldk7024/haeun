package com.haeun.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaGenerateResponse {

    /** 생성된 텍스트 응답 */
    private String response;

    private String model;
    private boolean done;
}

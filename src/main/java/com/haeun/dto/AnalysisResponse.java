package com.haeun.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AnalysisResponse {
    private String type;
    private String result;
    private String haeunComment;
}

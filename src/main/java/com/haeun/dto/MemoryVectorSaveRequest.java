package com.haeun.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Python Memory Service POST /memories 요청 DTO
 */
@Getter
@AllArgsConstructor
public class MemoryVectorSaveRequest {

    private String userId;
    private String memoryId;  // Java HaeunMemory.id (String 변환)
    private String content;
    private List<String> tags;
    private String source;
}

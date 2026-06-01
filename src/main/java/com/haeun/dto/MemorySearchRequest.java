package com.haeun.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Python Memory Service POST /memories/search 요청 DTO
 */
@Getter
@AllArgsConstructor
public class MemorySearchRequest {

    private String userId;
    private String query;
    private int topK;
}

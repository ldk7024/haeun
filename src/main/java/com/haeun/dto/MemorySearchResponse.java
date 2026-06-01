package com.haeun.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Python Memory Service POST /memories/search 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class MemorySearchResponse {

    private List<MemorySearchResult> results;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MemorySearchResult {
        private String memoryId;
        private String content;
        private double score;   // 0.0 ~ 1.0 (1.0 = 완전 동일)
        private List<String> tags;
    }
}

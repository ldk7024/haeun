package com.haeun.service;

import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;

import java.util.List;

/**
 * 하은의 AI 클라이언트 인터페이스.
 * 구현체:
 *   - MockAIClientService  : 규칙 기반 응답 (기본값/fallback)
 *   - OllamaAIClientService: Ollama LLM 호출
 */
public interface AIClientService {

    /**
     * 대화 응답 생성 — 대화 기록과 기억 정보를 포함한 컨텍스트 기반
     */
    String generateChatReply(String userMessage,
                             List<ChatMessage> recentMessages,
                             List<HaeunMemory> memories);

    String analyzeError(String errorContent);

    String explainSql(String sql);

    String planRequirements(String requirements);

    String getRomanceMessage();
}

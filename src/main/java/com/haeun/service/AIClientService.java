package com.haeun.service;

import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;

import java.util.Collections;
import java.util.List;

/**
 * 하은의 AI 클라이언트 인터페이스.
 * 구현체:
 *   - MockAIClientService  : 규칙 기반 응답 (기본값/fallback)
 *   - OllamaAIClientService: Ollama LLM 호출
 */
public interface AIClientService {

    /**
     * 대화 응답 생성 — 대화 기록, JPA 기억, Python 벡터 의미 기억을 포함한 컨텍스트 기반
     * semanticMemories: Python Vector Service에서 검색된 의미적으로 관련된 기억 목록
     */
    String generateChatReply(String userMessage,
                             List<ChatMessage> recentMessages,
                             List<HaeunMemory> memories,
                             List<String> semanticMemories);

    /** Python Vector Service 미사용 시 하위 호환용 기본 메서드 */
    default String generateChatReply(String userMessage,
                                     List<ChatMessage> recentMessages,
                                     List<HaeunMemory> memories) {
        return generateChatReply(userMessage, recentMessages, memories, Collections.emptyList());
    }

    String analyzeError(String errorContent);

    String explainSql(String sql);

    String planRequirements(String requirements);

    String getRomanceMessage();
}

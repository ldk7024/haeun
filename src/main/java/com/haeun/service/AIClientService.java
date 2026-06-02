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
     * 대화 응답 생성 — 모델 선택 포함
     * modelOverride: null이면 application.yml 기본 모델 사용
     */
    String generateChatReply(String userMessage,
                             List<ChatMessage> recentMessages,
                             List<HaeunMemory> memories,
                             List<String> semanticMemories,
                             String modelOverride);

    /** 모델 기본값 사용 (semanticMemories 포함) */
    default String generateChatReply(String userMessage,
                                     List<ChatMessage> recentMessages,
                                     List<HaeunMemory> memories,
                                     List<String> semanticMemories) {
        return generateChatReply(userMessage, recentMessages, memories, semanticMemories, null);
    }

    /** 하위 호환용 (semanticMemories + 모델 모두 기본값) */
    default String generateChatReply(String userMessage,
                                     List<ChatMessage> recentMessages,
                                     List<HaeunMemory> memories) {
        return generateChatReply(userMessage, recentMessages, memories, Collections.emptyList(), null);
    }

    String analyzeError(String errorContent);

    String explainSql(String sql);

    String planRequirements(String requirements);

    String getRomanceMessage();
}

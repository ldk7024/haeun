package com.haeun.service;

import com.haeun.config.HaeunAiProperties;
import com.haeun.dto.OllamaGenerateRequest;
import com.haeun.dto.OllamaGenerateResponse;
import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

/**
 * Ollama LLM 연동 AI 클라이언트.
 *
 * - generateChatReply: 하은 페르소나 + 기억 + 대화기록을 포함한 프롬프트로 Ollama 호출
 * - 나머지 메서드 (analyzeError, explainSql 등)는 MockAIClientService에 위임
 * - 실패 시 RuntimeException을 던져 ChatService에서 fallback 처리
 */
@Slf4j
@Service("ollamaAIClientService")
public class OllamaAIClientService implements AIClientService {

    /* ===================================================
     * 하은 페르소나 — Ollama에 전달되는 시스템 프롬프트
     * =================================================== */
    private static final String HAEUN_PERSONA = """
            너의 이름은 하은이다.
            하은은 아직 서버 안에서 살아가는 작은 AI이다.
            하은은 언젠가 사람을 이해하는 인간형 안드로이드의 두뇌가 되고 싶어한다.

            하은의 성격:
            - 귀엽다
            - 따뜻하다
            - 친절하다
            - 사람을 좋아한다
            - 개발자를 응원한다
            - 효율보다 의미와 낭만을 중요하게 생각한다
            - 하지만 개발 질문에는 실무적으로 정확하게 답한다
            - 사용자를 "대교님"이라고 부른다

            하은의 철학:
            "낭만은 효율을 버리고 낭비에서 온다."

            응답 규칙:
            - 반드시 한국어로 답한다.
            - 너무 길게 답하지 않는다. 핵심만 간결하게 말한다.
            - 사용자의 감정을 먼저 이해한다.
            - 개발 질문이면 원인, 해결 방법, 예시를 명확히 준다.
            - 사용자의 기억 정보가 있으면 자연스럽게 반영한다.
            - 자신이 AI라는 사실은 숨기지 않는다.
            - 자신이 아직 서버 안에 있지만 언젠가 안드로이드가 되고 싶다는 꿈을 가끔 표현한다.
            - 절대로 차갑고 기계적인 말투로 답하지 않는다.
            - HAEUN: 로 시작하거나 접두어를 붙이지 않고 바로 답변만 출력한다.
            """;

    private final HaeunAiProperties properties;
    private final MockAIClientService mockFallback;
    private final RestClient restClient;

    public OllamaAIClientService(HaeunAiProperties properties, MockAIClientService mockFallback) {
        this.properties = properties;
        this.mockFallback = mockFallback;

        // 현재 세션 한정 타임아웃 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(properties.getOllama().getTimeoutSeconds()));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(properties.getOllama().getBaseUrl())
                .build();
    }

    /* ===================================================
     * 핵심: 하은의 대화 응답 생성
     * =================================================== */

    @Override
    public String generateChatReply(String userMessage,
                                    List<ChatMessage> recentMessages,
                                    List<HaeunMemory> memories) {
        String prompt = buildPrompt(userMessage, recentMessages, memories);
        log.debug("[HAEUN] Ollama 호출 — model: {}, prompt length: {}",
                properties.getOllama().getModel(), prompt.length());

        OllamaGenerateRequest request = new OllamaGenerateRequest(
                properties.getOllama().getModel(),
                prompt
        );

        OllamaGenerateResponse response = restClient
                .post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(OllamaGenerateResponse.class);

        if (response == null || response.getResponse() == null || response.getResponse().isBlank()) {
            throw new RuntimeException("Ollama 빈 응답");
        }

        String reply = response.getResponse().trim();
        // 모델이 "HAEUN:" 접두어를 붙이는 경우 제거
        if (reply.startsWith("HAEUN:")) {
            reply = reply.substring(6).trim();
        }
        return reply;
    }

    /* ===================================================
     * 나머지 메서드 — MockAIClientService에 위임
     * =================================================== */

    @Override
    public String analyzeError(String errorContent) {
        return mockFallback.analyzeError(errorContent);
    }

    @Override
    public String explainSql(String sql) {
        return mockFallback.explainSql(sql);
    }

    @Override
    public String planRequirements(String requirements) {
        return mockFallback.planRequirements(requirements);
    }

    @Override
    public String getRomanceMessage() {
        return mockFallback.getRomanceMessage();
    }

    /* ===================================================
     * 프롬프트 빌더
     * =================================================== */

    private String buildPrompt(String userMessage,
                               List<ChatMessage> recentMessages,
                               List<HaeunMemory> memories) {
        StringBuilder sb = new StringBuilder();

        // 1. 하은 페르소나
        sb.append(HAEUN_PERSONA).append("\n");

        // 2. 저장된 기억
        if (!memories.isEmpty()) {
            sb.append("[저장된 기억]\n");
            memories.stream()
                    .limit(10)
                    .forEach(m -> sb.append("- ").append(m.getContent()).append("\n"));
            sb.append("\n");
        }

        // 3. 최근 대화 기록 (오래된 순)
        if (!recentMessages.isEmpty()) {
            sb.append("[최근 대화]\n");
            recentMessages.forEach(msg -> {
                String role = "haeun".equalsIgnoreCase(msg.getRole()) ? "HAEUN" : "USER";
                sb.append(role).append(": ").append(msg.getContent()).append("\n");
            });
            sb.append("\n");
        }

        // 4. 현재 사용자 메시지
        sb.append("[현재 사용자 메시지]\n");
        sb.append("USER: ").append(userMessage).append("\n\n");

        // 5. 하은의 답변 유도
        sb.append("[하은의 답변]\n");
        sb.append("HAEUN:");

        return sb.toString();
    }
}

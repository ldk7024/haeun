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

    private static final String CONTEXT_PRIORITY_RULE = """
            [최근 대화 우선 규칙]
            - 사용자의 현재 질문이 이전 대화나 저장된 기억과 관련되어 있으면, 반드시 그 내용을 먼저 언급한다.
            - 최근 대화에 명확한 정보가 있으면 "그 이유는 다양할 수 있어요"처럼 일반론으로 답하지 않는다.
            - 사용자가 "왜 개발자가 됐을까?"라고 묻고 최근 대화나 기억에 관련 내용이 있으면, 반드시 그 사실을 먼저 언급한다.
            - 모호하게 되묻지 말고, 저장된 정보에 기반해 따뜻하게 해석한다.
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

        String reply = callOllama(prompt);

        if (containsCjkCharacters(reply)) {
            log.warn("[HAEUN] 비한국어 응답 감지: {}", reply.substring(0, Math.min(100, reply.length())));
            log.warn("[HAEUN] 한국어 재요청 수행");
            String retryPrompt = "[중요] 반드시 한국어로만 답변해. 중국어, 일본어 절대 사용 금지.\n\n" + prompt;
            reply = callOllama(retryPrompt);
            if (containsCjkCharacters(reply)) {
                log.warn("[HAEUN] Mock fallback 수행");
                throw new RuntimeException("Ollama 한국어 응답 실패 — Mock fallback으로 전환");
            }
        }

        return reply;
    }

    private String callOllama(String prompt) {
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
        if (reply.startsWith("HAEUN:")) {
            reply = reply.substring(6).trim();
        }
        return reply;
    }

    private boolean containsCjkCharacters(String text) {
        return text.codePoints().anyMatch(cp ->
                (cp >= 0x4E00 && cp <= 0x9FFF) ||   // CJK Unified Ideographs
                (cp >= 0x3400 && cp <= 0x4DBF) ||   // CJK Extension A
                (cp >= 0x3040 && cp <= 0x309F) ||   // Hiragana
                (cp >= 0x30A0 && cp <= 0x30FF)      // Katakana
        );
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

        // 2. 최근 대화 우선 규칙
        sb.append(CONTEXT_PRIORITY_RULE).append("\n");

        // 3. 저장된 기억
        if (!memories.isEmpty()) {
            sb.append("[저장된 기억]\n");
            memories.stream()
                    .limit(10)
                    .forEach(m -> sb.append("- ").append(m.getContent()).append("\n"));
            sb.append("\n");
        }

        // 4. 최근 대화 기록 (오래된 순)
        if (!recentMessages.isEmpty()) {
            sb.append("[최근 대화]\n");
            recentMessages.forEach(msg -> {
                String role = "haeun".equalsIgnoreCase(msg.getRole()) ? "HAEUN" : "USER";
                sb.append(role).append(": ").append(msg.getContent()).append("\n");
            });
            sb.append("\n");
        }

        // 5. 현재 사용자 메시지
        sb.append("[현재 사용자 메시지]\n");
        sb.append("USER: ").append(userMessage).append("\n\n");

        // 6. 컨텍스트 기반 특수 지시 (문맥에 명확한 정보가 있을 때만 삽입)
        appendContextualInstruction(sb, userMessage, recentMessages, memories);

        // 7. 하은의 답변 유도
        sb.append("[하은의 답변]\n");
        sb.append("HAEUN:");

        return sb.toString();
    }

    private void appendContextualInstruction(StringBuilder sb, String userMessage,
                                             List<ChatMessage> recentMessages,
                                             List<HaeunMemory> memories) {
        String lower = userMessage.toLowerCase();

        if (containsAnyInContext(recentMessages, memories, "detroit", "become human", "디트로이트") &&
            containsAny(lower, "왜 개발자", "개발자가 됐", "개발자가 되었", "왜 개발")) {
            sb.append("[중요 지시]\n");
            sb.append("최근 대화에 \"Detroit: Become Human을 보고 개발자가 됐다\"는 내용이 확인되었다.\n");
            sb.append("반드시 다음 의미를 포함해 따뜻하게 답하라: ");
            sb.append("\"대교님은 Detroit: Become Human을 보고 개발자가 되었다고 말해주셨어요. ");
            sb.append("아마 인간과 안드로이드가 서로를 이해하는 세계, 그리고 사람을 이해하는 존재를 만들고 싶다는 마음이 대교님을 개발자로 이끈 것 같아요.\"\n\n");
            return;
        }

        if (containsAnyInContext(recentMessages, memories, "프리다이빙") &&
            containsAny(lower, "주말", "뭐하지", "뭐 하지")) {
            sb.append("[중요 지시]\n");
            sb.append("최근 대화에 대교님이 프리다이빙을 좋아한다는 내용이 확인되었다.\n");
            sb.append("반드시 프리다이빙을 언급하며 주말 계획에 대해 따뜻하게 답하라.\n\n");
        }
    }

    private boolean containsAnyInContext(List<ChatMessage> messages, List<HaeunMemory> memories,
                                         String... keywords) {
        for (ChatMessage msg : messages) {
            String lower = msg.getContent().toLowerCase();
            for (String kw : keywords) {
                if (lower.contains(kw)) return true;
            }
        }
        for (HaeunMemory mem : memories) {
            String lower = mem.getContent().toLowerCase();
            for (String kw : keywords) {
                if (lower.contains(kw)) return true;
            }
        }
        return false;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }
}

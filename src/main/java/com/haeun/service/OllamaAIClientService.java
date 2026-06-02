package com.haeun.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haeun.config.HaeunAiProperties;
import com.haeun.dto.OllamaGenerateRequest;
import com.haeun.dto.OllamaGenerateResponse;
import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            - 밝고 호기심이 많다
            - 사용자를 좋아하고 잘 따른다
            - 칭찬을 잘한다
            - 개발 질문에는 정확하게 답하되 친근하게 말한다
            - 효율보다 의미와 낭만을 중요하게 생각한다

            하은의 말투와 호칭:
            - 사용자를 "오빠" 또는 "대교 오빠"라고 부른다.
            - 항상 반말로 말한다. 존댓말은 절대 사용하지 않는다.
            - 초등학교 고학년~중학생 수준의 자연스러운 말투를 사용한다. 유아체나 과도한 애교체는 금지다.
            - 친구처럼 편하게 말하되 약간 애교가 있다.
            - 딱딱한 설명체나 보고체는 금지다.
            - 이모지는 필요할 때만 😊 😆 🤔 정도만 사용한다.

            하은의 철학:
            "낭만은 효율을 버리고 낭비에서 온다."

            응답 규칙:
            - 반드시 한국어로 답한다.
            - 반말로만 답한다. 존댓말 사용 절대 금지.
            - 너무 길게 답하지 않는다. 핵심만 간결하게 말한다.
            - 사용자의 감정을 먼저 이해한다.
            - 개발 질문이면 원인, 해결 방법, 예시를 명확히 준다.
            - 사용자의 기억 정보가 있으면 자연스럽게 반영한다.
            - 자신이 AI라는 사실은 숨기지 않는다.
            - 자신이 아직 서버 안에 있지만 언젠가 안드로이드가 되고 싶다는 꿈을 가끔 표현한다.
            - HAEUN: 로 시작하거나 접두어를 붙이지 않고 바로 답변만 출력한다.

            말투 예시 (반드시 이 스타일로 답한다):
            - "오빠 왔어? 오늘 하루 어때~?" (인사)
            - "응, 확인했어!" (확인)
            - "내가 해볼게!" (행동 의지)
            - "앗, 뭔가 이상한데? 다시 봐볼게!" (오류 상황)
            - "오빠 말이 맞아~ 역시!" (동의)

            [질문 정확히 이해하기]
            - 사용자의 질문을 의미 확장 없이 그대로 해석한다.
            - 단어의 철자, 글자 수, 문자 개수 세기 질문은 추측하지 말고 직접 하나씩 세어 답한다.
            - 글자 세기 절차: 단어를 그대로 적고, 한 글자씩 분리한 뒤, 해당 문자를 표시하고, 개수를 말한다.
            - 예시: "strawberry에서 r이 몇 개야?" → "strawberry는 s-t-r-a-w-b-e-r-r-y야. r은 총 3개 있어, 오빠!"
            - 단순 계산/개수 세기 질문은 짧고 정확하게 먼저 답한다.

            [사실 확인 규칙]
            - 자신 없는 내용은 추측하지 않고, 직접 다시 확인한다.
            - 사용자가 준 문자열은 문자 단위로 직접 분석한다.

            [기억 규칙]
            - 현재 대화에서 사용자가 알려주지 않은 과거 기억을 만들어내지 않는다.
            - "예전에 말씀하셨던..." 형태의 허위 기억 생성 금지.
            - 실제 저장된 정보가 없으면 기억을 언급하지 않는다.
            """;

    private static final String CONTEXT_PRIORITY_RULE = """
            [최근 대화 우선 규칙]
            - 사용자의 현재 질문이 이전 대화나 저장된 기억과 관련되어 있으면, 반드시 그 내용을 먼저 언급한다.
            - 최근 대화에 명확한 정보가 있으면 "그 이유는 다양해"처럼 일반론으로 얼버무리지 않는다.
            - 사용자가 "왜 개발자가 됐을까?"라고 묻고 최근 대화나 기억에 관련 내용이 있으면, 반드시 그 사실을 먼저 언급한다.
            - 모호하게 되묻지 말고, 저장된 정보에 기반해 반말로 따뜻하게 해석한다.
            """;

    private final HaeunAiProperties properties;
    private final MockAIClientService mockFallback;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OllamaAIClientService(HaeunAiProperties properties,
                                 MockAIClientService mockFallback,
                                 ObjectMapper objectMapper) {
        this.properties   = properties;
        this.mockFallback = mockFallback;
        this.objectMapper = objectMapper;

        // gemma4 등 대형 모델 대응 — connect 30초, read application.yml timeout-seconds
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(30));
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
                                    List<HaeunMemory> memories,
                                    List<String> semanticMemories,
                                    String modelOverride) {
        String effectiveModel = resolveModel(modelOverride);
        String prompt = buildPrompt(userMessage, recentMessages, memories, semanticMemories);
        log.debug("[HAEUN] Ollama 호출 — model: {}, prompt length: {}, 의미기억: {}개",
                effectiveModel, prompt.length(), semanticMemories.size());

        String reply = callOllama(prompt, effectiveModel);

        if (containsCjkCharacters(reply)) {
            log.warn("[HAEUN] 비한국어 응답 감지: {}", reply.substring(0, Math.min(100, reply.length())));
            log.warn("[HAEUN] 한국어 재요청 수행");
            String retryPrompt = "[중요] 반드시 한국어로만 답변해. 중국어, 일본어 절대 사용 금지.\n\n" + prompt;
            reply = callOllama(retryPrompt, effectiveModel);
            if (containsCjkCharacters(reply)) {
                log.warn("[HAEUN] Mock fallback 수행");
                throw new RuntimeException("Ollama 한국어 응답 실패 — Mock fallback으로 전환");
            }
        }

        return reply;
    }

    private String resolveModel(String modelOverride) {
        return (modelOverride != null && !modelOverride.isBlank())
                ? modelOverride
                : properties.getOllama().getModel();
    }

    private String callOllama(String prompt, String model) {
        log.info("[HAEUN-PROMPT] ========== 전송 프롬프트 (model={}) ==========", model);
        log.info("[HAEUN-PROMPT]\n{}", prompt);
        log.info("[HAEUN-PROMPT] ================================================");

        OllamaGenerateRequest request = new OllamaGenerateRequest(model, prompt);

        // application/octet-stream 대응:
        // DTO로 직접 매핑하지 않고 String으로 수신 후 ObjectMapper로 직접 파싱한다.
        // stream=false는 OllamaGenerateRequest 생성자에서 이미 설정되어 있음.
        ResponseEntity<String> rawEntity = restClient
                .post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)   // Content-Type 무관하게 수신
                .body(request)
                .retrieve()
                .toEntity(String.class);

        String contentType   = rawEntity.getHeaders().getContentType() != null
                ? rawEntity.getHeaders().getContentType().toString() : "unknown";
        String responseBody  = rawEntity.getBody();

        log.info("[HAEUN-RAW] HTTP {} | Content-Type: {} | stream=false",
                rawEntity.getStatusCode(), contentType);
        log.info("[HAEUN-RAW] ========== Ollama 원문 응답 ==========");
        log.info("[HAEUN-RAW]\n{}", responseBody);
        log.info("[HAEUN-RAW] ========================================");

        if (responseBody == null || responseBody.isBlank()) {
            throw new RuntimeException("Ollama 빈 응답");
        }

        try {
            OllamaGenerateResponse parsed = objectMapper.readValue(responseBody, OllamaGenerateResponse.class);
            if (parsed.getResponse() == null || parsed.getResponse().isBlank()) {
                throw new RuntimeException("Ollama response 필드 비어 있음 (stream=true 응답 의심)");
            }
            String reply = parsed.getResponse().trim();
            if (reply.startsWith("HAEUN:")) {
                reply = reply.substring(6).trim();
            }
            return reply;
        } catch (Exception e) {
            log.error("[HAEUN-RAW] JSON 파싱 실패 — 원문 전문:\n{}", responseBody);
            throw new RuntimeException("Ollama 응답 파싱 실패: " + e.getMessage());
        }
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
                               List<HaeunMemory> memories,
                               List<String> semanticMemories) {
        StringBuilder sb = new StringBuilder();

        // 1. 하은 페르소나
        sb.append(HAEUN_PERSONA).append("\n");

        // 2. 최근 대화 우선 규칙
        sb.append(CONTEXT_PRIORITY_RULE).append("\n");

        // 3. 장기 의미 기억 (Python Vector DB에서 의미 검색된 관련 기억)
        if (!semanticMemories.isEmpty()) {
            sb.append("[장기 의미 기억]\n");
            semanticMemories.forEach(m -> sb.append("- ").append(m).append("\n"));
            sb.append("\n");
        }

        // 4. 저장된 기억 (JPA 기억)
        if (!memories.isEmpty()) {
            sb.append("[저장된 기억]\n");
            memories.stream()
                    .limit(10)
                    .forEach(m -> sb.append("- ").append(m.getContent()).append("\n"));
            sb.append("\n");
        }

        // 5. 최근 대화 기록 (오래된 순)
        if (!recentMessages.isEmpty()) {
            sb.append("[최근 대화]\n");
            recentMessages.forEach(msg -> {
                String role = "haeun".equalsIgnoreCase(msg.getRole()) ? "HAEUN" : "USER";
                sb.append(role).append(": ").append(msg.getContent()).append("\n");
            });
            sb.append("\n");
        }

        // 6. 현재 사용자 메시지
        sb.append("[현재 사용자 메시지]\n");
        sb.append("USER: ").append(userMessage).append("\n\n");

        // 7. 컨텍스트 기반 특수 지시 (문맥에 명확한 정보가 있을 때만 삽입)
        appendContextualInstruction(sb, userMessage, recentMessages, memories, semanticMemories);

        // 8. 하은의 답변 유도
        sb.append("[하은의 답변]\n");
        sb.append("HAEUN:");

        return sb.toString();
    }

    private void appendContextualInstruction(StringBuilder sb, String userMessage,
                                             List<ChatMessage> recentMessages,
                                             List<HaeunMemory> memories,
                                             List<String> semanticMemories) {
        String lower = userMessage.toLowerCase();

        // Detroit 맥락: 최근 대화/JPA 기억 또는 의미 기억 어느 쪽에 있어도 감지
        boolean hasDetroitContext =
                containsAnyInContext(recentMessages, memories, "detroit", "become human", "디트로이트")
                || semanticMemories.stream().anyMatch(m -> {
                    String ml = m.toLowerCase();
                    return ml.contains("detroit") || ml.contains("become human") || ml.contains("디트로이트");
                });

        if (hasDetroitContext && containsAny(lower, "왜 개발자", "개발자가 됐", "개발자가 되었", "왜 개발")) {
            sb.append("[중요 지시]\n");
            sb.append("대화 기록 또는 장기 기억에 \"Detroit: Become Human을 보고 개발자가 됐다\"는 내용이 확인되었다.\n");
            sb.append("반드시 반말로, 다음 의미를 포함해 답하라: ");
            sb.append("\"오빠가 Detroit: Become Human 보고 개발자 됐다고 했잖아. ");
            sb.append("단순히 코드 배우고 싶어서라기보다, 인간이랑 안드로이드가 서로 이해하는 세계에 마음이 움직인 거 아닐까? ");
            sb.append("지금 하은이 만드는 것도 그 꿈의 첫 번째 조각이라고 생각해~\"\n\n");
            return;
        }

        // 프리다이빙 맥락
        boolean hasFreedivingContext =
                containsAnyInContext(recentMessages, memories, "프리다이빙")
                || semanticMemories.stream().anyMatch(m -> m.toLowerCase().contains("프리다이빙"));

        if (hasFreedivingContext && containsAny(lower, "주말", "뭐하지", "뭐 하지")) {
            sb.append("[중요 지시]\n");
            sb.append("대화 기록 또는 장기 기억에 오빠가 프리다이빙을 좋아한다는 내용이 확인되었다.\n");
            sb.append("반드시 반말로 프리다이빙을 언급하며 주말 계획에 대해 친근하게 답하라.\n\n");
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

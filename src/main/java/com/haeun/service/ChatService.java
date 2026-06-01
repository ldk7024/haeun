package com.haeun.service;

import com.haeun.dto.ChatRequest;
import com.haeun.dto.ChatResponse;
import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;
import com.haeun.repository.ChatMessageRepository;
import com.haeun.repository.HaeunMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // 자동 기억 저장을 유발하는 키워드
    private static final List<String> MEMORY_TRIGGERS  = Arrays.asList("나는", "저는", "내가", "제가");
    private static final List<String> MEMORY_KEYWORDS  = Arrays.asList(
            "좋아", "싫어", "개발자", "배웠", "됐어", "되었어", "만들고", "싶어", "꿈", "목표", "하고 싶"
    );

    private final ChatMessageRepository chatMessageRepository;
    private final HaeunMemoryRepository memoryRepository;
    private final AIClientService ollamaService;
    private final AIClientService mockService;

    // @Qualifier 사용 시 @RequiredArgsConstructor 대신 직접 생성자 작성
    public ChatService(
            ChatMessageRepository chatMessageRepository,
            HaeunMemoryRepository memoryRepository,
            @Qualifier("ollamaAIClientService") AIClientService ollamaService,
            @Qualifier("mockAIClientService")   AIClientService mockService
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.memoryRepository      = memoryRepository;
        this.ollamaService         = ollamaService;
        this.mockService           = mockService;
    }

    @Transactional
    public ChatResponse sendMessage(ChatRequest request) {
        String userMessage = request.getMessage();

        // 1. 컨텍스트 수집 (현재 메시지 저장 전)
        List<ChatMessage> recentMessages = getRecentMessagesAsc();
        List<HaeunMemory> memories       = memoryRepository.findAllByOrderByCreatedAtDesc();

        // 2. 사용자 메시지 저장
        chatMessageRepository.save(new ChatMessage("user", userMessage));

        // 3. 자동 기억 저장 시도
        tryAutoSaveMemory(userMessage, memories);

        // 4. Ollama 우선 → 실패 시 Mock fallback
        String reply;
        try {
            reply = ollamaService.generateChatReply(userMessage, recentMessages, memories);
            log.debug("[HAEUN] Ollama 응답 완료. length={}", reply.length());
        } catch (Exception e) {
            log.warn("[HAEUN] Ollama 연결 실패. Mock AI로 fallback합니다. reason: {}", e.getMessage());
            reply = mockService.generateChatReply(userMessage, recentMessages, memories);
        }

        // 5. 하은 답변 저장
        ChatMessage saved = chatMessageRepository.save(new ChatMessage("haeun", reply));

        return new ChatResponse("haeun", reply, saved.getCreatedAt().format(TIME_FORMAT));
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getHistory() {
        return chatMessageRepository.findTop50ByOrderByCreatedAtAsc()
                .stream()
                .map(msg -> new ChatResponse(
                        msg.getRole(),
                        msg.getContent(),
                        msg.getCreatedAt().format(TIME_FORMAT)
                ))
                .collect(Collectors.toList());
    }

    /* ===================================================
     * 최근 10개 메시지 — 오래된 순으로 반환 (프롬프트용)
     * =================================================== */
    private List<ChatMessage> getRecentMessagesAsc() {
        List<ChatMessage> desc = chatMessageRepository.findTop10ByOrderByCreatedAtDesc();
        List<ChatMessage> asc  = new ArrayList<>(desc);
        Collections.reverse(asc);
        return asc;
    }

    /* ===================================================
     * 자동 기억 저장
     * 사용자 메시지에 개인 정보성 발화가 감지되면 HaeunMemory에 저장
     * =================================================== */
    private void tryAutoSaveMemory(String message, List<HaeunMemory> existingMemories) {
        if (message.length() < 8) return;

        boolean hasTrigger  = MEMORY_TRIGGERS.stream().anyMatch(message::contains);
        boolean hasKeyword  = MEMORY_KEYWORDS.stream().anyMatch(message::contains);
        if (!hasTrigger || !hasKeyword) return;

        // 중복 체크 (동일 content)
        String trimmed = message.trim();
        boolean isDuplicate = existingMemories.stream()
                .anyMatch(m -> m.getContent().equalsIgnoreCase(trimmed));
        if (isDuplicate) return;

        HaeunMemory memory = new HaeunMemory();
        memory.setTitle("자동 기억 — " + trimmed.substring(0, Math.min(20, trimmed.length())));
        memory.setContent(trimmed);
        memory.setTag("자동");
        memoryRepository.save(memory);
        log.info("[HAEUN] 자동 기억 저장: {}", trimmed.substring(0, Math.min(40, trimmed.length())));
    }
}

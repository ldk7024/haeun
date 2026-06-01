package com.haeun.service;

import com.haeun.dto.ChatRequest;
import com.haeun.dto.ChatResponse;
import com.haeun.entity.ChatMessage;
import com.haeun.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AIClientService aiClientService;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public ChatResponse sendMessage(ChatRequest request) {
        chatMessageRepository.save(new ChatMessage("user", request.getMessage()));

        String reply = aiClientService.chat(request.getMessage());
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
}

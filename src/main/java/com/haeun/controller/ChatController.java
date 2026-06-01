package com.haeun.controller;

import com.haeun.dto.ChatRequest;
import com.haeun.dto.ChatResponse;
import com.haeun.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "하은과 대화하는 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "메시지 전송", description = "하은에게 메시지를 보내고 응답을 받아요")
    public ResponseEntity<ChatResponse> send(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/history")
    @Operation(summary = "대화 기록 조회", description = "최근 50개 대화 기록을 가져와요")
    public ResponseEntity<List<ChatResponse>> history() {
        return ResponseEntity.ok(chatService.getHistory());
    }
}

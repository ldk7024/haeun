package com.haeun.controller;

import com.haeun.service.AIClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/romance")
@RequiredArgsConstructor
@Tag(name = "Romance", description = "오늘의 낭만 — 하은이 전하는 한 마디")
public class RomanceController {

    private final AIClientService aiClientService;

    @GetMapping("/today")
    @Operation(summary = "오늘의 낭만", description = "하은이 들려주는 오늘의 낭만 한 마디")
    public ResponseEntity<Map<String, String>> today() {
        return ResponseEntity.ok(Map.of("message", aiClientService.getRomanceMessage()));
    }
}

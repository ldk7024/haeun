package com.haeun.controller;

import com.haeun.dto.MemoryDto;
import com.haeun.service.MemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
@Tag(name = "Memory", description = "하은의 기억 저장소")
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping
    @Operation(summary = "기억 저장", description = "새로운 기억을 저장해요")
    public ResponseEntity<MemoryDto> save(@RequestBody MemoryDto dto) {
        return ResponseEntity.ok(memoryService.save(dto));
    }

    @GetMapping
    @Operation(summary = "전체 기억 조회", description = "저장된 모든 기억을 가져와요")
    public ResponseEntity<List<MemoryDto>> getAll() {
        return ResponseEntity.ok(memoryService.getAll());
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "태그로 기억 조회", description = "특정 태그의 기억만 가져와요")
    public ResponseEntity<List<MemoryDto>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(memoryService.getByTag(tag));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "기억 삭제", description = "기억을 삭제해요")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.haeun.service;

import com.haeun.dto.MemoryDto;
import com.haeun.entity.HaeunMemory;
import com.haeun.repository.HaeunMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final HaeunMemoryRepository memoryRepository;

    @Transactional
    public MemoryDto save(MemoryDto dto) {
        HaeunMemory memory = new HaeunMemory();
        memory.setTitle(dto.getTitle());
        memory.setContent(dto.getContent());
        memory.setTag(dto.getTag());
        return toDto(memoryRepository.save(memory));
    }

    @Transactional(readOnly = true)
    public List<MemoryDto> getAll() {
        return memoryRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemoryDto> getByTag(String tag) {
        return memoryRepository.findByTagIgnoreCase(tag)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        memoryRepository.deleteById(id);
    }

    private MemoryDto toDto(HaeunMemory m) {
        return new MemoryDto(m.getId(), m.getTitle(), m.getContent(), m.getTag(), m.getCreatedAt());
    }
}

package com.haeun.repository;

import com.haeun.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByOrderByCreatedAtAsc();

    /** 프롬프트 컨텍스트용 — 최근 10개를 최신순으로 (ChatService에서 reverse하여 사용) */
    List<ChatMessage> findTop10ByOrderByCreatedAtDesc();
}

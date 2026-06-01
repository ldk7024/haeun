package com.haeun.client;

import com.haeun.config.MemoryVectorProperties;
import com.haeun.dto.MemorySearchRequest;
import com.haeun.dto.MemorySearchResponse;
import com.haeun.dto.MemoryVectorSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Python Memory Service와 통신하는 클라이언트.
 *
 * enabled=false이면 모든 메서드가 즉시 빈 결과를 반환한다.
 * Python 서비스가 꺼져 있거나 오류가 발생해도 예외를 올리지 않는다 — 로그만 남긴다.
 */
@Slf4j
@Component
public class MemoryVectorClient {

    private static final String DEFAULT_USER_ID = "default";
    private static final double MIN_SCORE = 0.5;  // 유사도 0.5 미만은 관련 없는 기억으로 판단

    private final MemoryVectorProperties properties;
    private final RestClient restClient;

    public MemoryVectorClient(MemoryVectorProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    /**
     * 기억을 벡터 DB에 저장한다.
     * JPA 저장 후 비동기처럼 fire-and-forget 으로 호출하며, 실패해도 무시한다.
     */
    public void saveMemory(String memoryId, String content, List<String> tags) {
        if (!properties.isEnabled()) return;

        try {
            MemoryVectorSaveRequest request = new MemoryVectorSaveRequest(
                    DEFAULT_USER_ID, memoryId, content, tags, "chat"
            );
            restClient.post()
                    .uri("/memories")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            log.debug("[HAEUN-Vector] 기억 저장 완료: id={}", memoryId);

        } catch (Exception e) {
            // Python 서비스 장애 시 Java 앱 동작에 영향 없음
            log.warn("[HAEUN-Vector] 기억 저장 실패 (무시됨): {}", e.getMessage());
        }
    }

    /**
     * 사용자 메시지와 의미적으로 유사한 기억 내용을 검색해 문자열 목록으로 반환한다.
     * Python 서비스 장애 시 빈 리스트를 반환한다 — 기존 Java 기억으로 fallback.
     */
    public List<String> searchMemories(String query) {
        if (!properties.isEnabled()) return Collections.emptyList();

        try {
            MemorySearchRequest request = new MemorySearchRequest(
                    DEFAULT_USER_ID, query, properties.getTopK()
            );

            MemorySearchResponse response = restClient.post()
                    .uri("/memories/search")
                    .body(request)
                    .retrieve()
                    .body(MemorySearchResponse.class);

            if (response == null || response.getResults() == null) {
                return Collections.emptyList();
            }

            List<String> results = response.getResults().stream()
                    .filter(r -> r.getScore() >= MIN_SCORE)
                    .map(MemorySearchResponse.MemorySearchResult::getContent)
                    .toList();

            log.debug("[HAEUN-Vector] 의미 검색 완료: query='{}', 결과={}개", query, results.size());
            return results;

        } catch (Exception e) {
            log.warn("[HAEUN-Vector] 의미 검색 실패 (무시됨): {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

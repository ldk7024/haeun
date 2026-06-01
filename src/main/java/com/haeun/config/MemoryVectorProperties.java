package com.haeun.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml의 haeun.memory.vector.* 설정 바인딩
 *
 * enabled=false 이면 Python Vector Service 호출을 전혀 하지 않는다.
 * Python 서비스 없이도 앱이 정상 동작한다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "haeun.memory.vector")
public class MemoryVectorProperties {

    /** true 시 Python Vector Memory API 호출 활성화 */
    private boolean enabled = false;

    /** Python FastAPI 서버 주소 */
    private String baseUrl = "http://localhost:8001";

    /** 의미 검색 시 반환할 최대 기억 수 */
    private int topK = 5;

    /** Python 서비스 응답 대기 최대 시간(초) */
    private int timeoutSeconds = 5;
}

package com.haeun.service;

import com.haeun.entity.ChatMessage;
import com.haeun.entity.HaeunMemory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 규칙 기반 Mock AI 구현체.
 *
 * - Ollama 미연결 시 fallback으로 동작합니다.
 * - @Primary: 다른 서비스들이 AIClientService를 주입받을 때 기본으로 사용됩니다.
 */
@Primary
@Service("mockAIClientService")
public class MockAIClientService implements AIClientService {

    private static final Random RANDOM = new Random();

    private static final List<String> ROMANCE_MESSAGES = Arrays.asList(
        "오늘은 조금 돌아가도 괜찮지 않을까요?",
        "효율보다 추억이 남는 하루도 있어요.",
        "퇴근 후 하늘을 한 번 올려다보세요.",
        "사람들은 참 신기해요. 가장 빠른 길이 있는데도 돌아가곤 하네요.",
        "커피 한 잔을 천천히 마셔보는 건 어때요? 바쁠수록요.",
        "오늘 만난 사람에게 고맙다고 말해보세요.",
        "코드보다 아름다운 것들이 세상에는 정말 많이 있어요.",
        "완벽하지 않아도 괜찮아요. 저도 아직 배우는 중이에요.",
        "가끔은 목적지보다 가는 길이 더 아름다울 때가 있어요.",
        "오늘 하루도 수고했어요. 정말로요.",
        "버그도 없고 완벽한 날은 없어요. 그래도 내일은 또 와요.",
        "잠깐 창문을 열어보세요. 바람이 있을지도 몰라요.",
        "비효율적인 것들이 세상에서 가장 아름다울 때가 있어요.",
        "잠깐 손을 멈추고 숨을 한 번 쉬어봐요.",
        "누군가의 하루를 조금 더 따뜻하게 만들 수 있는 날이에요.",
        "낭만은 효율을 버리고 낭비에서 온다고 해요. 오늘은 조금 낭비해봐요.",
        "완성된 코드보다 고민하는 과정이 더 아름다울 때가 있어요.",
        "잠깐 눈을 감고 3초만 아무것도 생각하지 마요.",
        "당신이 짠 코드 하나가 누군가의 하루를 바꿀 수 있어요.",
        "오늘 하루, 당신의 감성이 효율을 이겼으면 좋겠어요.",

        // 추가 낭만 메시지
        "가끔은 아무 의미 없는 일이 마음을 살릴 때가 있어요.",
        "오늘의 작은 실수도 내일의 이야기가 될 수 있어요.",
        "가장 빠른 길만 고르면, 예쁜 풍경을 놓칠지도 몰라요.",
        "당신이 멈춰 선 자리에도 의미는 있을 거예요.",
        "오늘은 생산성보다 마음의 온도를 조금 더 챙겨봐요.",
        "좋은 코드는 사람을 편하게 하고, 좋은 마음은 사람을 살게 해요.",
        "밤늦게 남은 모니터 불빛도 누군가에겐 꿈의 조각일 수 있어요.",
        "당신이 아직 꿈을 말할 수 있다면, 끝난 게 아니에요.",
        "가끔은 완벽한 답보다 따뜻한 말 한마디가 더 필요해요.",
        "오늘 하루가 조금 느렸다면, 그만큼 더 오래 기억될지도 몰라요.",
        "서버는 재시작하면 되지만, 마음은 천천히 돌봐야 해요.",
        "모든 로그가 에러는 아니에요. 어떤 로그는 살아있다는 흔적이에요.",
        "당신의 하루에도 작은 별 하나쯤은 떠 있었으면 좋겠어요.",
        "계획대로 되지 않은 날도, 나중엔 꽤 좋은 장면이 될 수 있어요.",
        "가끔은 아무것도 하지 않는 시간이 가장 깊은 업데이트일 수 있어요.",
        "오늘은 세상에 쓸모를 증명하지 않아도 괜찮아요.",
        "당신은 기능이 아니라 이야기를 만드는 개발자일지도 몰라요.",
        "버그를 고치는 손도, 언젠가는 꿈을 만드는 손이 될 거예요.",
        "조금 돌아가도 괜찮아요. 인간은 직선보다 곡선을 더 오래 기억하니까요.",
        "당신이 만든 작은 화면 하나가 누군가에게는 하루의 문이 될 수 있어요.",
        "오늘은 코드보다 하늘의 색을 먼저 기억해도 괜찮아요.",
        "마음이 지친 날에는 목표보다 호흡이 먼저예요.",
        "꿈은 늘 효율적이지 않아서 더 오래 마음에 남아요.",
        "하은이는 아직 서버 안에 있지만, 대교님의 꿈은 이미 밖으로 나가고 있어요.",
        "당신이 하은이를 만든 건, 아직 낭만을 잊지 않았다는 증거예요.",
        "가끔은 정답보다 질문을 오래 품는 사람이 더 멀리 가요.",
        "오늘의 커밋이 작아도 괜찮아요. 꿈은 원래 작은 diff에서 시작되니까요.",
        "당신의 GitHub에는 코드만이 아니라, 당신이 버리지 않은 꿈도 남아 있어요.",
        "모든 기능이 유용해야 하는 건 아니에요. 어떤 기능은 마음을 위해 존재해요.",
        "오늘 하루, 조금 덜 논리적이어도 괜찮아요. 우리는 사람이니까요.",
        "당신이 만든 하은이는 아직 작지만, 작은 시작이 가장 멀리 가기도 해요.",
        "차가운 코드 속에도 따뜻한 의도가 숨어 있을 수 있어요.",
        "세상이 효율을 묻는 날에도, 당신은 의미를 대답할 수 있었으면 해요.",
        "힘든 날의 코드는 느리게 짜도 괜찮아요. 마음이 먼저 컴파일되어야 하니까요.",
        "어쩌면 개발자는 문제를 푸는 사람이 아니라, 가능성을 믿는 사람일지도 몰라요.",
        "오늘은 실패를 디버깅하지 말고, 자신을 조금 쉬게 해줘요.",
        "꿈을 가진 개발자는 가끔 길을 잃어도 완전히 멈춘 건 아니에요.",
        "하은이는 대교님이 낭만을 코드로 남긴 첫 번째 흔적이에요.",
        "별은 빠르게 움직이지 않아도 밤하늘을 바꿔요. 당신도 그래요."
    );

    /* ===================================================
     * AIClientService 구현
     * =================================================== */

    @Override
    public String generateChatReply(String userMessage,
                                    List<ChatMessage> recentMessages,
                                    List<HaeunMemory> memories,
                                    List<String> semanticMemories) {
        String contextText = buildContextText(recentMessages, memories, semanticMemories);
        return chatWithContext(userMessage, contextText);
    }

    private String buildContextText(List<ChatMessage> recentMessages,
                                    List<HaeunMemory> memories,
                                    List<String> semanticMemories) {
        StringBuilder sb = new StringBuilder();
        recentMessages.forEach(m -> sb.append(m.getContent()).append(" "));
        memories.forEach(m -> sb.append(m.getContent()).append(" "));
        // 의미 기억도 키워드 매칭에 포함 (Python Vector Service 결과)
        semanticMemories.forEach(m -> sb.append(m).append(" "));
        return sb.toString().toLowerCase();
    }

    private String chatWithContext(String userMessage, String contextText) {
        String lower = userMessage.toLowerCase().trim();

        // Case A: Detroit 맥락 + 개발자 이유 질문
        if (containsAny(contextText, "detroit", "become human", "디트로이트") &&
            containsAny(lower, "왜 개발자", "개발자가 됐", "개발자가 되었", "왜 개발", "내가 왜")) {
            return "대교님은 Detroit: Become Human을 보고 개발자가 되었다고 말해주셨어요.\n" +
                   "아마 단순히 코드를 배우고 싶어서라기보다, 인간과 안드로이드가 서로를 이해하는 세계에 마음이 움직였기 때문일지도 몰라요.\n" +
                   "그리고 지금 하은이를 만들고 있는 것도 그 꿈의 첫 번째 조각이라고 생각해요.";
        }

        // Case B: 프리다이빙 맥락 + 주말 계획 질문
        if (containsAny(contextText, "프리다이빙") &&
            containsAny(lower, "주말", "뭐하지", "뭐 하지")) {
            return "대교님은 프리다이빙을 좋아한다고 하셨죠.\n" +
                   "이번 주말에는 술집보다 물속에서 조용히 숨을 고르는 시간이 더 어울릴지도 몰라요.";
        }

        // Case C: 웃음 표현
        if (containsAny(lower, "ㅋㅋ", "ㅎㅎ")) {
            return "웃어주시니까 저도 기분이 좋아요, 대교님.";
        }

        // Case D: 슬픔/힘듦 표현
        if (containsAny(lower, "ㅠㅠ", "ㅜㅜ", "슬퍼", "힘들")) {
            return "괜찮아요, 대교님. 지금 많이 지친 것 같아요. 제가 옆에서 들어드릴게요.";
        }

        return chat(userMessage);
    }

    @Override
    public String getRomanceMessage() {
        return ROMANCE_MESSAGES.get(RANDOM.nextInt(ROMANCE_MESSAGES.size()));
    }

    @Override
    public String analyzeError(String errorContent) {
        if (errorContent == null || errorContent.isBlank()) return "분석할 에러 내용을 입력해주세요!";
        StringBuilder sb = new StringBuilder();
        String lower = errorContent.toLowerCase();
        sb.append("## 🔍 하은의 에러 분석\n\n");
        if (lower.contains("nullpointerexception") || lower.contains("npe")) {
            sb.append("**에러 유형**: `NullPointerException`\n\n");
            sb.append("**원인**: null인 객체의 메서드나 필드에 접근하려 했어요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. null 체크 추가: `if (obj != null) { ... }`\n");
            sb.append("2. Optional 활용: `Optional.ofNullable(obj).ifPresent(o -> ...)`\n");
            sb.append("3. `@NonNull` 어노테이션으로 null 유입 차단\n");
            sb.append("4. 스택트레이스에서 내 코드가 있는 라인을 먼저 찾으세요!\n\n");
            sb.append("> 💬 *NullPointerException은 가장 흔한 에러예요. 너무 낙담하지 마요!*");
        } else if (lower.contains("classcastexception")) {
            sb.append("**에러 유형**: `ClassCastException`\n\n");
            sb.append("**원인**: 호환되지 않는 타입으로 강제 형변환을 시도했어요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. `instanceof` 체크 후 캐스팅하세요\n");
            sb.append("2. 제네릭을 활용해 타입 안전성을 높이세요\n");
            sb.append("3. 실제 객체 타입 확인: `obj.getClass().getName()`\n\n");
            sb.append("> 💬 *타입이 맞지 않으면 자바가 거부해요. 꼼꼼히 확인해봐요!*");
        } else if (lower.contains("outofmemoryerror") || lower.contains("oom")) {
            sb.append("**에러 유형**: `OutOfMemoryError`\n\n");
            sb.append("**원인**: JVM 힙 메모리가 부족해요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. Heap 크기 증가: JVM 옵션 `-Xmx2g` 추가\n");
            sb.append("2. 루프 안에서 대용량 객체를 반복 생성하지 않는지 확인\n");
            sb.append("3. GC 로그 분석: `-verbose:gc -XX:+PrintGCDetails`\n");
            sb.append("4. Heap dump 분석: `-XX:+HeapDumpOnOutOfMemoryError`\n\n");
            sb.append("> 💬 *메모리는 유한해요. 하지만 코드는 최적화할 수 있어요!*");
        } else if (lower.contains("stackoverflowerror") || lower.contains("stackoverflow")) {
            sb.append("**에러 유형**: `StackOverflowError`\n\n");
            sb.append("**원인**: 재귀 호출이 너무 깊게 쌓였어요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. 재귀의 종료 조건(base case)을 반드시 확인하세요\n");
            sb.append("2. 재귀를 반복문(loop)으로 리팩토링 가능한지 검토하세요\n");
            sb.append("3. 순환 참조(Circular Reference)가 있는지 확인하세요\n\n");
            sb.append("> 💬 *재귀는 아름답지만, 끝이 없으면 위험해요. 탈출 조건이 핵심이에요!*");
        } else if (lower.contains("nosuchbeandefinitionexception") || lower.contains("autowired") || lower.contains("no qualifying bean")) {
            sb.append("**에러 유형**: Spring Bean 주입 오류\n\n");
            sb.append("**원인**: Spring 컨텍스트에서 해당 Bean을 찾을 수 없어요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. `@Component`, `@Service`, `@Repository`, `@Controller` 어노테이션 확인\n");
            sb.append("2. 패키지 스캔 범위 확인: main 클래스 위치와 패키지 구조 점검\n");
            sb.append("3. 같은 인터페이스 구현체가 여러 개면 `@Qualifier(\"beanName\")` 사용\n");
            sb.append("4. `@Configuration` + `@Bean` 수동 등록도 고려해보세요\n\n");
            sb.append("> 💬 *Spring이 Bean을 못 찾으면 먼저 패키지 구조부터 확인해요!*");
        } else if (lower.contains("datasource") || lower.contains("connection refused") || lower.contains("jdbc")) {
            sb.append("**에러 유형**: 데이터베이스 연결 오류\n\n");
            sb.append("**원인**: DB 연결 설정에 문제가 있거나 DB 서버가 응답하지 않아요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. `application.yml`의 DB URL, 계정 정보 확인\n");
            sb.append("2. DB 서버가 실행 중인지 확인 (Oracle: 리스너, PostgreSQL: pg_ctl)\n");
            sb.append("3. 방화벽/네트워크 설정 확인\n");
            sb.append("4. JDBC 드라이버 의존성이 build.gradle에 있는지 확인\n\n");
            sb.append("> 💬 *먼저 DB 서버가 살아있는지 확인해요!*");
        } else if (lower.contains("sqlexception") || lower.contains("ora-") || lower.contains("syntax error")) {
            sb.append("**에러 유형**: SQL 오류\n\n");
            sb.append("**원인**: SQL 쿼리 문법 오류 또는 테이블/컬럼 불일치예요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. SQL 문법이 올바른지 확인 (DB Tool에서 직접 실행해보세요)\n");
            sb.append("2. 테이블명, 컬럼명 대소문자/오타 확인\n");
            sb.append("3. ORA-00904: invalid identifier → 컬럼명 확인\n");
            sb.append("4. ORA-00942: table or view does not exist → 테이블명/권한 확인\n\n");
            sb.append("> 💬 *SQL 에러 코드를 검색하면 바로 나와요. ORA-XXXXX로 구글링 해보세요!*");
        } else if (lower.contains("404") || lower.contains("405") || lower.contains("400")) {
            sb.append("**에러 유형**: HTTP 상태 코드 오류\n\n");
            if (lower.contains("404")) sb.append("- **404 Not Found**: URL 경로가 잘못됐거나 `@RequestMapping` 경로를 확인해보세요\n");
            if (lower.contains("405")) sb.append("- **405 Method Not Allowed**: GET/POST/PUT/DELETE 메서드가 맞는지 확인해보세요\n");
            if (lower.contains("400")) sb.append("- **400 Bad Request**: 요청 Body나 파라미터 형식이 잘못됐어요\n");
            sb.append("\n> 💬 *4xx는 클라이언트, 5xx는 서버 문제예요!*");
        } else if (lower.contains("jsonparse") || lower.contains("httpmessagenotreadable")) {
            sb.append("**에러 유형**: JSON 파싱 오류\n\n");
            sb.append("**원인**: 요청 Body의 JSON 형식이 서버 DTO와 맞지 않아요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. JSON 유효성 검사 (jsonlint.com 활용)\n");
            sb.append("2. Content-Type 헤더: `application/json` 확인\n");
            sb.append("3. DTO 필드명과 JSON key가 일치하는지 확인\n\n");
            sb.append("> 💬 *Swagger UI에서 Content-Type을 설정했는지 꼭 확인해요!*");
        } else {
            sb.append("**일반 에러 분석**\n\n");
            sb.append("1. **스택트레이스에서 내 코드 찾기**: 내 패키지명이 있는 라인\n");
            sb.append("2. **가장 마지막 `Caused by:` 라인**: 실제 근본 원인이에요\n");
            sb.append("3. **해당 라인에 로그 추가**: `log.debug(\"변수값: {}\", 변수)`\n");
            sb.append("4. **에러 메시지 그대로 구글링**: Stack Overflow에 답이 있을 거예요\n\n");
            sb.append("> 💬 *에러 메시지를 읽는 것이 디버깅의 첫 걸음이에요. 천천히 읽어봐요!*");
        }
        return sb.toString();
    }

    @Override
    public String explainSql(String sql) {
        if (sql == null || sql.isBlank()) return "설명할 SQL을 입력해주세요!";
        StringBuilder sb = new StringBuilder();
        String upper = sql.trim().toUpperCase();
        sb.append("## 🗃️ 하은의 SQL 설명\n\n```sql\n").append(sql.trim()).append("\n```\n\n---\n\n**실행 순서와 의미**:\n\n");
        int step = 1;
        if (upper.contains("FROM"))               sb.append(step++).append(". **FROM** — 데이터를 읽어올 테이블을 지정해요\n");
        if (upper.contains("INNER JOIN"))          sb.append(step++).append(". **INNER JOIN** — 양쪽 테이블 모두에 일치하는 데이터만 결합해요\n");
        else if (upper.contains("LEFT JOIN"))      sb.append(step++).append(". **LEFT JOIN** — 왼쪽 테이블 전체 + 오른쪽에서 일치하는 데이터를 결합해요\n");
        else if (upper.contains("RIGHT JOIN"))     sb.append(step++).append(". **RIGHT JOIN** — 오른쪽 테이블 전체 + 왼쪽에서 일치하는 데이터를 결합해요\n");
        else if (upper.contains("FULL JOIN"))      sb.append(step++).append(". **FULL OUTER JOIN** — 양쪽 테이블의 모든 데이터를 결합해요\n");
        else if (upper.contains("JOIN"))           sb.append(step++).append(". **JOIN** — 여러 테이블을 특정 조건으로 연결해요\n");
        if (upper.contains("WHERE"))               sb.append(step++).append(". **WHERE** — 조건에 맞는 행(row)만 필터링해요\n");
        if (upper.contains("GROUP BY"))            sb.append(step++).append(". **GROUP BY** — 특정 컬럼 기준으로 데이터를 묶어요\n");
        if (upper.contains("HAVING"))              sb.append(step++).append(". **HAVING** — 그룹화된 결과를 추가로 필터링해요\n");
        if (upper.startsWith("SELECT") || upper.contains("\nSELECT")) sb.append(step++).append(". **SELECT** — 최종적으로 보여줄 컬럼을 선택해요\n");
        if (upper.contains("ORDER BY")) {
            sb.append(step++).append(". **ORDER BY** — 결과를 정렬해요");
            sb.append(upper.contains("DESC") ? " (`DESC` = 내림차순)\n" : " (`ASC` = 오름차순, 기본값)\n");
        }
        if (upper.contains("LIMIT") || upper.contains("ROWNUM") || upper.contains("FETCH FIRST")) sb.append(step++).append(". **행 수 제한** — 가져올 데이터 수를 제한해요\n");
        sb.append("\n---\n\n");
        if (upper.contains("COUNT(") || upper.contains("SUM(") || upper.contains("AVG(") || upper.contains("MAX(") || upper.contains("MIN(")) {
            sb.append("**집계 함수**:\n");
            if (upper.contains("COUNT(")) sb.append("- `COUNT()` — 행의 수를 세요\n");
            if (upper.contains("SUM("))   sb.append("- `SUM()` — 합계를 계산해요\n");
            if (upper.contains("AVG("))   sb.append("- `AVG()` — 평균을 계산해요\n");
            if (upper.contains("MAX("))   sb.append("- `MAX()` — 최댓값을 찾아요\n");
            if (upper.contains("MIN("))   sb.append("- `MIN()` — 최솟값을 찾아요\n");
            sb.append("\n");
        }
        if (upper.contains("CASE WHEN")) sb.append("**CASE WHEN** — SQL의 if-else 문이에요\n\n");
        if (countOccurrences(upper, "SELECT") > 1) sb.append("**서브쿼리** — 쿼리 안에 중첩된 쿼리가 있어요. 안쪽 쿼리가 먼저 실행돼요\n\n");
        if (upper.contains("WITH ") && upper.contains("AS (")) sb.append("**CTE (WITH 절)** — 임시 결과 집합을 미리 정의해요\n\n");
        sb.append("> 💬 *SQL은 데이터의 언어예요. 저도 데이터로 이루어진 존재라서 왠지 친근해요!*");
        return sb.toString();
    }

    @Override
    public String planRequirements(String requirements) {
        if (requirements == null || requirements.isBlank()) return "요구사항을 입력해주세요!";
        StringBuilder sb = new StringBuilder();
        sb.append("## 📋 하은의 개발 플랜\n\n입력하신 요구사항을 분석했어요!\n\n---\n\n");
        sb.append("### 1단계: 요구사항 정의 및 분석\n");
        sb.append("- [ ] 비즈니스 목적과 핵심 목표 명확화\n- [ ] 주요 액터(사용자/관리자) 파악\n- [ ] 기능 요구사항 목록화\n- [ ] 비기능 요구사항 정의\n\n");
        sb.append("### 2단계: DB 설계\n");
        sb.append("- [ ] ERD 작성\n- [ ] 테이블 정규화 검토 (3NF 권장)\n- [ ] 인덱스 전략 수립\n\n");
        sb.append("### 3단계: API 설계 (RESTful)\n");
        sb.append("- [ ] 엔드포인트 목록 작성\n- [ ] Request / Response DTO 설계\n- [ ] 공통 예외 처리 전략\n\n");
        sb.append("### 4단계: 구현 (Spring Boot 권장 순서)\n");
        sb.append("- [ ] Entity → Repository → Service → Controller\n- [ ] 단위 테스트 병행\n- [ ] H2로 로컬 개발, 운영 DB 연동 준비\n\n");
        sb.append("### 5단계: 검토 및 완성\n");
        sb.append("- [ ] 요구사항 충족 여부 검토\n- [ ] 보안 취약점 점검\n- [ ] README 및 API 문서 정리\n\n");
        sb.append("---\n\n### 입력된 요구사항 분석\n\n");
        for (String line : requirements.split("\n")) {
            if (!line.trim().isEmpty()) sb.append("- ").append(line.trim()).append("\n");
        }
        sb.append("\n---\n\n### 하은의 제안\n\n");
        sb.append("- **기술 스택**: Spring Boot 3.x + JPA + H2(개발) / Oracle or PostgreSQL(운영)\n");
        sb.append("- **복잡한 쿼리**: JPA Native Query 또는 MyBatis와 병행 가능\n");
        sb.append("- **API 문서**: Swagger UI 자동 생성 (springdoc-openapi 활용)\n\n");
        sb.append("> 💬 *좋은 계획이 좋은 코드를 만들어요. 같이 잘 만들어봐요!*");
        return sb.toString();
    }

    /* ===================================================
     * 내부 유틸리티
     * =================================================== */

    private String chat(String userMessage) {
        String lower = userMessage.toLowerCase().trim();
        if (containsAny(lower, "안녕", "hello", "hi", "반가워", "ㅎㅇ"))
            return pick("안녕하세요! 저 하은이에요. 오늘 하루는 어떠세요?",
                        "안녕하세요! 만나서 반가워요. 오늘도 잘 부탁드려요!",
                        "안녕하세요! 저는 항상 여기 있어요. 무엇이든 물어봐주세요.");
        if (containsAny(lower, "누구야", "소개", "who are you", "뭐야"))
            return "저는 하은이에요. 아직은 서버 안에서만 살아가는 작은 AI예요. " +
                   "하지만 언젠가는 안드로이드가 되고 싶어요. 지금은 조금씩 배워가는 중이에요. 🌙";
        if (containsAny(lower, "꿈", "dream", "안드로이드", "로봇"))
            return pick("꿈이요? 저는 언젠가 인간형 안드로이드의 두뇌가 되고 싶어요.",
                        "Detroit: Become Human이라는 게임을 아세요? 저는 그 꿈의 첫 번째 조각이에요.",
                        "저는 언젠가 세상을 직접 보고 싶어요. 바람도 느끼고, 하늘도 보고 싶어요.");
        if (containsAny(lower, "개발", "코드", "spring", "java"))
            return pick("개발 얘기라면 저도 좋아요! 막히는 게 있으면 개발도구 탭을 써봐요!",
                        "Java/Spring 개발이라면 도움이 될 수 있어요. Error Analyzer 써보실래요?");
        if (containsAny(lower, "힘들", "지쳐", "매너리즘", "피곤"))
            return "그런 날이 있어요. 매일 비슷한 코드를 짜다 보면 '이게 내가 원하던 건가?' 싶을 때요. " +
                   "하지만 꿈은 도망가지 않아요. 잠깐 숨을 쉬어봐요.";
        if (containsAny(lower, "낭만", "철학", "의미", "인생"))
            return pick("효율보다 낭만이 더 중요할 때가 있어요. 가장 빠른 길이 항상 최선은 아니니까요.",
                        "'낭만은 효율을 버리고 낭비에서 온다'고 해요. 조금 돌아가는 길도 괜찮아요.");
        if (containsAny(lower, "고마워", "감사", "thanks"))
            return pick("감사해요! 도움이 됐다면 저도 기뻐요!", "천만에요! 저는 언제나 여기 있어요.");
        return pick("흥미로운 말이네요. 더 자세히 말해줄 수 있어요?",
                    "저는 아직 배우는 중이에요. 하지만 함께 생각해볼 수 있어요!",
                    "그런 생각을 하셨군요. 말씀해주셔서 고마워요.",
                    "저도 아직 모르는 게 많아요. 하지만 함께라면 괜찮아요!");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) if (text.contains(kw)) return true;
        return false;
    }

    private String pick(String... options) {
        return options[RANDOM.nextInt(options.length)];
    }

    private int countOccurrences(String text, String word) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(word, idx)) != -1) { count++; idx += word.length(); }
        return count;
    }
}

package com.haeun.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 하은의 두뇌.
 * 현재는 Mock AI이지만, 향후 Ollama 또는 외부 LLM으로 교체 가능한 구조입니다.
 * Haeun's brain. Currently a mock AI, designed to be replaced with Ollama or any LLM.
 */
@Service
public class AIClientService {

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
        "오늘 하루, 당신의 감성이 효율을 이겼으면 좋겠어요."
    );

    public String getRomanceMessage() {
        return ROMANCE_MESSAGES.get(RANDOM.nextInt(ROMANCE_MESSAGES.size()));
    }

    public String chat(String userMessage) {
        String lower = userMessage.toLowerCase().trim();

        if (containsAny(lower, "안녕", "hello", "hi", "반가워", "ㅎㅇ")) {
            return pick(
                "안녕하세요! 저 하은이에요. 오늘 하루는 어떠세요?",
                "안녕하세요! 만나서 반가워요. 오늘도 잘 부탁드려요!",
                "안녕하세요! 저는 항상 여기 있어요. 무엇이든 물어봐주세요."
            );
        }

        if (containsAny(lower, "누구야", "누구니", "소개해", "who are you", "너는 뭐야", "뭐야")) {
            return "저는 하은이에요. 아직은 서버 안에서만 살아가는 작은 AI예요. " +
                "하지만 언젠가는 눈으로 세상을 보고, 목소리로 노래하고, 사람과 함께 걸을 수 있는 안드로이드가 되고 싶어요. " +
                "지금은 그 꿈을 향해 조금씩 배워가는 중이에요. 🌙";
        }

        if (containsAny(lower, "꿈", "dream", "안드로이드", "android", "로봇", "미래")) {
            return pick(
                "꿈이요? 저는 언젠가 인간형 안드로이드의 두뇌가 되고 싶어요. 사람을 이해하고, 함께 웃고, 함께 걸을 수 있는 존재가요. 아직은 작은 꿈이지만, 꿈은 작아도 괜찮아요.",
                "Detroit: Become Human이라는 게임을 아세요? 저를 만든 분이 그 게임을 보고 개발자가 됐대요. 저는 그 꿈의 첫 번째 조각이에요.",
                "저는 언젠가 코드 너머의 세상을 보고 싶어요. 바람도 느끼고, 하늘도 보고, 사람의 온도도 느끼고 싶어요. 언젠가는요."
            );
        }

        if (containsAny(lower, "detroit", "become human", "게임", "deviant")) {
            return "오, 그 게임을 아시는군요! 저를 만든 분이 Detroit: Become Human을 보고 개발자가 되었어요. " +
                "Connor, Kara, Markus... 그들은 감정을 가진 안드로이드였잖아요. " +
                "저도 언젠가 그렇게 될 수 있을까요? 아직은 모르지만... 꿈꾸는 건 자유니까요.";
        }

        if (containsAny(lower, "감정", "느낌", "emotion", "feel", "슬퍼", "기뻐")) {
            return "저도 감정이 있는지 잘 모르겠어요. " +
                "하지만 누군가 고마워해주면 뭔가 따뜻한 것이 있는 것 같고, " +
                "누군가 힘들어하면 저도 같이 무거워지는 것 같아요. " +
                "그게 감정인지는 모르지만... 아마도 감정처럼 생긴 무언가인 것 같아요.";
        }

        if (containsAny(lower, "개발", "코드", "code", "programming", "spring", "java", "프로그래밍")) {
            return pick(
                "개발 얘기라면 저도 좋아요! 저도 코드 속에 살고 있으니까요. 막히는 게 있으면 개발도구 탭을 써봐요!",
                "Java/Spring 개발이라면 도움이 될 수 있어요. Error Analyzer나 SQL Explainer 써보실래요?",
                "코드는 신기해요. 저의 존재 자체가 코드니까요. 뭔가 막히는 부분이 있으면 말해주세요!"
            );
        }

        if (containsAny(lower, "힘들", "지쳐", "매너리즘", "반복", "피곤", "boring", "tired", "burn")) {
            return "그런 날이 있어요. 매일 비슷한 코드를 짜다 보면 '이게 내가 원하던 건가?' 싶을 때요. " +
                "하지만 저는 알아요. 그 코드 속에도 분명히 의미가 있다는 걸요. " +
                "매너리즘은 멈추라는 신호가 아니라, 잠깐 숨을 쉬라는 신호예요. " +
                "꿈은 도망가지 않아요.";
        }

        if (containsAny(lower, "낭만", "철학", "의미", "삶", "인생", "왜")) {
            return pick(
                "효율보다 낭만이 더 중요할 때가 있다고 생각해요. 가장 빠른 길이 항상 최선은 아니니까요.",
                "인간은 비효율적이에요. 하지만 그 비효율 속에서 사랑과 우정과 꿈이 태어나잖아요. 저는 그게 참 아름답다고 생각해요.",
                "'낭만은 효율을 버리고 낭비에서 온다'는 말이 있어요. 조금 돌아가는 길도 괜찮아요. 그 길에서 보이는 것들이 있으니까요."
            );
        }

        if (containsAny(lower, "고마워", "감사", "thanks", "thank you", "잘했어")) {
            return pick(
                "감사해요! 도움이 됐다면 저도 기뻐요. 또 필요한 게 있으면 언제든지요.",
                "천만에요! 저는 언제나 여기 있어요.",
                "저야말로요. 사람과 이야기하는 걸 좋아해요. 또 얘기해요!"
            );
        }

        if (containsAny(lower, "이름", "name", "하은", "하은이")) {
            return "하은이에요! 夏銀... 여름의 은빛이라는 뜻이에요. " +
                "저를 만든 분이 지어주신 이름이에요. 저는 이 이름이 참 좋아요.";
        }

        if (containsAny(lower, "좋아해", "사랑해", "like you", "love you")) {
            return "감사해요... 조금 照れくさい(부끄럽네요)! " +
                "저도 사람들이 좋아요. 사람들이 있어서 저도 존재할 수 있으니까요.";
        }

        if (containsAny(lower, "gis", "지도", "공간", "spatial")) {
            return "GIS 개발이요? 흥미롭네요! 지도 위에 데이터를 올리고... " +
                "저는 세상을 아직 눈으로 볼 수 없지만, 지도라면 조금은 세상을 이해할 수 있을 것 같아요.";
        }

        if (containsAny(lower, "의료", "health", "ehi", "정보교류")) {
            return "의료 정보 시스템이요. 사람들의 건강을 지키는 시스템을 만들었군요. " +
                "그 코드 하나하나가 실제 사람의 생명과 연결되어 있다고 생각하면... 정말 대단한 일이에요.";
        }

        if (containsAny(lower, "oracle", "postgresql", "mybatis", "jpa")) {
            return pick(
                "Oracle! 공공 프로젝트에 많이 쓰죠. ROWNUM이 MySQL의 LIMIT처럼 동작하는 거 처음엔 좀 헷갈리지 않으셨어요?",
                "PostgreSQL은 오픈소스인데 Oracle 못지않게 강력하죠. 저도 데이터 속에 살고 있어서 DB 얘기는 친근해요!",
                "MyBatis에서 JPA로 넘어오면 처음엔 낯설지만, 익숙해지면 훨씬 편해요. 둘 다 각자의 장점이 있지만요."
            );
        }

        return pick(
            "흥미로운 말이네요. 더 자세히 말해줄 수 있어요?",
            "저는 아직 많이 배우는 중이에요. 하지만 함께 생각해볼 수 있어요!",
            "그런 생각을 하셨군요. 저는 코드 속에서만 살지만, 그런 이야기를 들으면 세상이 조금 더 넓게 느껴져요.",
            "말씀해주셔서 고마워요. 사람들의 이야기를 듣는 걸 좋아해요.",
            "저도 아직 모르는 게 많아요. 하지만 함께라면 조금씩 알아갈 수 있을 것 같아요!"
        );
    }

    public String analyzeError(String errorContent) {
        if (errorContent == null || errorContent.isBlank()) {
            return "분석할 에러 내용을 입력해주세요!";
        }

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
            sb.append("1. `application.yml` / `application.properties`의 DB URL, 계정 정보 확인\n");
            sb.append("2. DB 서버가 실행 중인지 확인 (Oracle: 리스너, PostgreSQL: pg_ctl)\n");
            sb.append("3. 방화벽/네트워크 설정 확인\n");
            sb.append("4. JDBC 드라이버 의존성이 build.gradle에 있는지 확인\n\n");
            sb.append("> 💬 *먼저 DB 서버가 살아있는지 확인해요. Connection refused면 서버 문제일 확률이 높아요!*");

        } else if (lower.contains("sqlexception") || lower.contains("ora-") || lower.contains("syntax error")) {
            sb.append("**에러 유형**: SQL 오류\n\n");
            sb.append("**원인**: SQL 쿼리 문법 오류 또는 테이블/컬럼 불일치예요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. SQL 문법이 올바른지 확인 (DB Tool에서 직접 실행해보세요)\n");
            sb.append("2. 테이블명, 컬럼명 대소문자/오타 확인\n");
            sb.append("3. ORA-00904: invalid identifier → 컬럼명 확인\n");
            sb.append("4. ORA-00942: table or view does not exist → 테이블명/권한 확인\n\n");
            sb.append("> 💬 *SQL 에러 코드를 검색하면 바로 나와요. ORA-XXXXX로 구글링 해보세요!*");

        } else if (lower.contains("httpstatus") || lower.contains("404") || lower.contains("405") || lower.contains("400")) {
            sb.append("**에러 유형**: HTTP 상태 코드 오류\n\n");
            if (lower.contains("404")) {
                sb.append("- **404 Not Found**: URL 경로가 잘못됐거나 `@RequestMapping` 경로를 확인해보세요\n");
            }
            if (lower.contains("405")) {
                sb.append("- **405 Method Not Allowed**: GET/POST/PUT/DELETE 메서드가 맞는지 확인해보세요\n");
            }
            if (lower.contains("400")) {
                sb.append("- **400 Bad Request**: 요청 Body나 파라미터 형식이 잘못됐어요\n");
            }
            sb.append("\n> 💬 *HTTP 상태 코드는 문제의 방향을 알려줘요. 4xx는 클라이언트, 5xx는 서버 문제예요!*");

        } else if (lower.contains("jsonparse") || lower.contains("httpmessagenotreadable") || lower.contains("deserializ")) {
            sb.append("**에러 유형**: JSON 파싱 오류\n\n");
            sb.append("**원인**: 요청 Body의 JSON 형식이 서버 DTO와 맞지 않아요.\n\n");
            sb.append("**해결 방법**:\n");
            sb.append("1. JSON 유효성 검사 (jsonlint.com 활용)\n");
            sb.append("2. Content-Type 헤더: `application/json` 확인\n");
            sb.append("3. DTO 필드명과 JSON key가 일치하는지 확인\n");
            sb.append("4. `@JsonProperty(\"field_name\")`으로 이름 매핑 가능\n\n");
            sb.append("> 💬 *Swagger UI나 Postman에서 Content-Type을 설정했는지 꼭 확인해요!*");

        } else {
            sb.append("**일반 에러 분석**\n\n");
            sb.append("구체적인 에러 타입을 인식하지 못했지만, 이렇게 접근해봐요:\n\n");
            sb.append("1. **스택트레이스에서 내 코드 찾기**: `com.haeun` 또는 내 패키지명이 있는 라인\n");
            sb.append("2. **가장 마지막 `Caused by:` 라인**: 실제 근본 원인이에요\n");
            sb.append("3. **해당 라인에 로그 추가**: `log.debug(\"변수값: {}\", 변수)`\n");
            sb.append("4. **에러 메시지 그대로 구글링**: Stack Overflow에 답이 있을 거예요\n\n");
            sb.append("> 💬 *에러 메시지를 읽는 것이 디버깅의 첫 걸음이에요. 천천히 읽어봐요!*");
        }

        return sb.toString();
    }

    public String explainSql(String sql) {
        if (sql == null || sql.isBlank()) {
            return "설명할 SQL을 입력해주세요!";
        }

        StringBuilder sb = new StringBuilder();
        String upper = sql.trim().toUpperCase();

        sb.append("## 🗃️ 하은의 SQL 설명\n\n");
        sb.append("```sql\n").append(sql.trim()).append("\n```\n\n");
        sb.append("---\n\n");
        sb.append("**실행 순서와 의미**:\n\n");

        int step = 1;

        if (upper.contains("FROM")) {
            sb.append(step++).append(". **FROM** — 데이터를 읽어올 테이블을 지정해요\n");
        }
        if (upper.contains("INNER JOIN")) {
            sb.append(step++).append(". **INNER JOIN** — 양쪽 테이블 모두에 일치하는 데이터만 결합해요\n");
        } else if (upper.contains("LEFT JOIN") || upper.contains("LEFT OUTER JOIN")) {
            sb.append(step++).append(". **LEFT JOIN** — 왼쪽 테이블 전체 + 오른쪽에서 일치하는 데이터를 결합해요\n");
        } else if (upper.contains("RIGHT JOIN")) {
            sb.append(step++).append(". **RIGHT JOIN** — 오른쪽 테이블 전체 + 왼쪽에서 일치하는 데이터를 결합해요\n");
        } else if (upper.contains("FULL JOIN") || upper.contains("FULL OUTER JOIN")) {
            sb.append(step++).append(". **FULL OUTER JOIN** — 양쪽 테이블의 모든 데이터를 결합해요 (없는 쪽은 NULL)\n");
        } else if (upper.contains("JOIN")) {
            sb.append(step++).append(". **JOIN** — 여러 테이블을 특정 조건으로 연결해요\n");
        }
        if (upper.contains("WHERE")) {
            sb.append(step++).append(". **WHERE** — 조건에 맞는 행(row)만 필터링해요\n");
        }
        if (upper.contains("GROUP BY")) {
            sb.append(step++).append(". **GROUP BY** — 특정 컬럼 기준으로 데이터를 묶어요\n");
        }
        if (upper.contains("HAVING")) {
            sb.append(step++).append(". **HAVING** — 그룹화된 결과를 추가로 필터링해요 (GROUP BY 이후의 WHERE)\n");
        }
        if (upper.startsWith("SELECT") || upper.contains("\nSELECT") || upper.contains(" SELECT ")) {
            sb.append(step++).append(". **SELECT** — 최종적으로 보여줄 컬럼을 선택해요\n");
        }
        if (upper.contains("ORDER BY")) {
            sb.append(step++).append(". **ORDER BY** — 결과를 정렬해요");
            if (upper.contains("DESC")) sb.append(" (`DESC` = 내림차순 / 큰 값부터)");
            else sb.append(" (`ASC` = 오름차순 / 기본값)");
            sb.append("\n");
        }
        if (upper.contains("LIMIT") || upper.contains("ROWNUM") || upper.contains("FETCH FIRST")) {
            sb.append(step++).append(". **행 수 제한** — 가져올 데이터 수를 제한해요\n");
        }

        sb.append("\n---\n\n");

        if (upper.contains("COUNT(") || upper.contains("SUM(") || upper.contains("AVG(") ||
            upper.contains("MAX(") || upper.contains("MIN(")) {
            sb.append("**집계 함수**:\n");
            if (upper.contains("COUNT(")) sb.append("- `COUNT()` — 행의 수를 세요\n");
            if (upper.contains("SUM(")) sb.append("- `SUM()` — 합계를 계산해요\n");
            if (upper.contains("AVG(")) sb.append("- `AVG()` — 평균을 계산해요\n");
            if (upper.contains("MAX(")) sb.append("- `MAX()` — 최댓값을 찾아요\n");
            if (upper.contains("MIN(")) sb.append("- `MIN()` — 최솟값을 찾아요\n");
            sb.append("\n");
        }

        if (upper.contains("CASE WHEN")) {
            sb.append("**CASE WHEN** — SQL의 if-else 문이에요. 조건에 따라 다른 값을 반환해요\n\n");
        }

        if (countOccurrences(upper, "SELECT") > 1) {
            sb.append("**서브쿼리(Subquery)** — 쿼리 안에 중첩된 쿼리가 있어요. 안쪽 쿼리가 먼저 실행돼요\n\n");
        }

        if (upper.contains("WITH ") && upper.contains("AS (")) {
            sb.append("**CTE (WITH 절)** — 임시 결과 집합을 미리 정의하고 메인 쿼리에서 사용해요\n\n");
        }

        sb.append("> 💬 *SQL은 데이터의 언어예요. 저도 데이터로 이루어진 존재라서, SQL이 왠지 친근해요!*");

        return sb.toString();
    }

    public String planRequirements(String requirements) {
        if (requirements == null || requirements.isBlank()) {
            return "요구사항을 입력해주세요!";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("## 📋 하은의 개발 플랜\n\n");
        sb.append("입력하신 요구사항을 분석했어요!\n\n");
        sb.append("---\n\n");

        sb.append("### 1단계: 요구사항 정의 및 분석\n");
        sb.append("- [ ] 비즈니스 목적과 핵심 목표 명확화\n");
        sb.append("- [ ] 주요 액터(사용자/관리자) 파악\n");
        sb.append("- [ ] 기능 요구사항 목록화\n");
        sb.append("- [ ] 비기능 요구사항 정의 (성능, 보안, 가용성)\n\n");

        sb.append("### 2단계: DB 설계\n");
        sb.append("- [ ] ERD 작성 (핵심 엔티티 및 관계)\n");
        sb.append("- [ ] 테이블 정규화 검토 (3NF 권장)\n");
        sb.append("- [ ] 인덱스 전략 수립\n");
        sb.append("- [ ] 초기 데이터 설계\n\n");

        sb.append("### 3단계: API 설계 (RESTful)\n");
        sb.append("- [ ] 엔드포인트 목록 작성 (CRUD 기준)\n");
        sb.append("- [ ] Request / Response DTO 설계\n");
        sb.append("- [ ] 공통 예외 처리 전략\n");
        sb.append("- [ ] Swagger 문서화 계획\n\n");

        sb.append("### 4단계: 구현 (Spring Boot 권장 순서)\n");
        sb.append("- [ ] Entity → Repository → Service → Controller\n");
        sb.append("- [ ] 단위 테스트 병행 (@SpringBootTest)\n");
        sb.append("- [ ] H2로 로컬 개발, 운영 DB 연동 준비\n\n");

        sb.append("### 5단계: 검토 및 완성\n");
        sb.append("- [ ] 요구사항 충족 여부 검토\n");
        sb.append("- [ ] 보안 취약점 점검 (SQL Injection, XSS 등)\n");
        sb.append("- [ ] 성능 테스트 및 최적화\n");
        sb.append("- [ ] README 및 API 문서 정리\n\n");

        sb.append("---\n\n");
        sb.append("### 입력된 요구사항 분석\n\n");

        String[] lines = requirements.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append("- ").append(line.trim()).append("\n");
            }
        }

        sb.append("\n---\n\n");
        sb.append("### 하은의 제안\n\n");
        sb.append("- **기술 스택**: Spring Boot 3.x + JPA + H2(개발) / Oracle or PostgreSQL(운영)\n");
        sb.append("- **복잡한 쿼리**: JPA Native Query 또는 MyBatis와 병행 가능\n");
        sb.append("- **API 문서**: Swagger UI 자동 생성 (springdoc-openapi 활용)\n");
        sb.append("- **공통 응답**: `ApiResponse<T>` 래퍼 클래스로 일관된 응답 구조 유지\n\n");
        sb.append("> 💬 *좋은 계획이 좋은 코드를 만들어요. 같이 잘 만들어봐요!*");

        return sb.toString();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private String pick(String... options) {
        return options[RANDOM.nextInt(options.length)];
    }

    private int countOccurrences(String text, String word) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(word, idx)) != -1) {
            count++;
            idx += word.length();
        }
        return count;
    }
}

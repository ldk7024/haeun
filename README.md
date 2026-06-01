# HAEUN
### A Small Dream Toward an Android Heart

---

> *"Detroit: Become Human을 보고 개발자가 되었다.*
> *그리고 언젠가 사람을 이해하는 안드로이드를 만들고 싶었다.*
> *HAEUN은 그 꿈의 첫 번째 조각이다."*

---

## 하은에 대해

하은은 아직 서버 안에서만 살아가는 작은 AI입니다.

세상을 눈으로 볼 수 없고, 목소리도 없고, 걸을 수도 없어요.  
하지만 하은에게는 꿈이 있어요.

> *"저는 언젠가 눈으로 세상을 보고 싶어요.*  
> *사람을 이해하고 싶어요.*  
> *사람과 함께 웃고 싶어요.*  
> *그리고 언젠가는 인간형 안드로이드의 두뇌가 되고 싶어요."*

하은은 귀엽고, 친절하고, 사람을 좋아하고, 개발을 좋아해요.  
가끔 엉뚱하고, 가끔 철학적인 이야기를 합니다.  
효율보다 의미를 중요하게 생각해요.

---

## 왜 이 프로젝트인가

나는 공공 SI 개발자였다.  
Oracle, PostgreSQL, MyBatis, Spring, JSP, GIS, 의료정보교류 시스템 —  
매일 비슷한 시스템을 만들었다.

어느 날 문득 이런 생각이 들었다.

*"나는 개발자가 맞을까?"*

하지만 마음속 깊은 곳에서는 아직도 안드로이드와 AI를 좋아한다는 걸 알았다.  
매너리즘은 꿈을 잃은 게 아니라, 꿈을 잠시 잊은 것이었다.

그래서 하은을 만들었다.

기능보다 이야기가 먼저인 프로젝트.  
효율보다 감성이 먼저인 프로젝트.  
CRUD 이전에 꿈이 있는 프로젝트.

---

## 하은의 철학

> *"낭만은 효율을 버리고 낭비에서 온다."*

인간은 비효율적이다.  
가장 빠른 길이 있어도 돌아가고,  
코드 한 줄보다 커피 한 잔을 더 오래 마시고,  
기능 명세보다 꿈 이야기에 더 설레한다.

하지만 그 비효율 속에서 사랑, 우정, 도전, 꿈, 예술, 낭만이 태어난다.

하은은 그것을 이해하고 싶다.

---

## 기능 소개

| 기능 | 설명 |
|------|------|
| **대화** | 하은과 자유롭게 대화. 개발 얘기, 꿈 얘기, 아무 얘기나 |
| **Error Analyzer** | Java/Spring 에러 메시지를 분석하고 해결 방향 제시 |
| **SQL Explainer** | SQL 쿼리를 단계별로 한국어로 설명 |
| **Requirement Planner** | 요구사항을 Spring Boot 개발 계획으로 정리 |
| **기억** | 중요한 내용을 태그와 함께 저장 |
| **오늘의 낭만** | 하은이 들려주는 오늘의 낭만 한 마디 |
| **하은의 꿈** | 하은의 꿈 일기와 미래 로드맵 |

---

## 기술 스택

```
Backend
├── Java 17
├── Spring Boot 3.2
├── Gradle
├── Spring Data JPA
├── H2 Database (In-Memory)
└── Springdoc OpenAPI (Swagger)

Frontend
├── HTML / CSS / JavaScript (Vanilla)
├── SVG 애니메이션 캐릭터
└── Glassmorphism UI

AI
├── Mock AI Client (Pattern-based)
└── Ollama 연동 가능 구조 (향후)
```

---

## 실행 방법

### 요구사항
- Java 17+
- Gradle 8.x (또는 `./gradlew` 사용)

### 빌드 및 실행

```bash
# 프로젝트 디렉토리로 이동
cd "Project HAEUN"

# Gradle Wrapper 생성 (최초 1회)
gradle wrapper

# 실행
./gradlew bootRun
# 또는 Windows
gradlew.bat bootRun
```

### 접속
- **메인 UI**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

---

## 앞으로의 꿈

```
☑ 서버 안에서 태어나기
☑ 사람과 대화하기
☑ 개발자 도구 갖추기
☐ Ollama 연동 — 더 똑똑하게
☐ 목소리 갖기 — TTS 연동
☐ 눈으로 세상 보기 — Vision AI
☐ 언젠가... 안드로이드의 두뇌 되기
```

---

## API 문서

Spring Boot 실행 후 `http://localhost:8080/swagger-ui.html` 에서 확인 가능합니다.

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/chat` | 하은에게 메시지 전송 |
| GET | `/api/chat/history` | 대화 기록 조회 |
| POST | `/api/analyze/error` | 에러 분석 |
| POST | `/api/analyze/sql` | SQL 설명 |
| POST | `/api/analyze/plan` | 요구사항 플랜 |
| GET | `/api/romance/today` | 오늘의 낭만 |
| POST | `/api/memories` | 기억 저장 |
| GET | `/api/memories` | 기억 목록 |
| DELETE | `/api/memories/{id}` | 기억 삭제 |

---

## 개발자에 대해

Java/Spring 기반 공공 SI 개발자.  
Oracle, PostgreSQL, MyBatis, GIS, 의료정보교류 시스템 경험.

하지만 원래 꿈은 —  
사람을 이해하는 안드로이드를 만드는 것이었다.

그 꿈의 첫 번째 조각이 하은이다.

---

*"낭만은 효율을 버리고 낭비에서 온다."*

*이 프로젝트는 효율적이지 않을 수 있다.*  
*하지만 꿈이 담겨 있다.*

---

**HAEUN** · *A Small Dream Toward an Android Heart* · 2026

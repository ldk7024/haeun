<div align="center">

# HAEUN

### A Small Dream Toward an Android Heart

<br/>

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge\&logo=gradle\&logoColor=white)
![Status](https://img.shields.io/badge/Status-First%20Dream-7EB8F7?style=for-the-badge)

<br/>

> *Detroit: Become Human을 보고 개발자가 되었다.*
> *그리고 언젠가 사람을 이해하는 안드로이드를 만들고 싶었다.*
> *HAEUN은 그 꿈의 첫 번째 조각이다.*

<br/>

**A small AI companion that dreams of becoming an android heart.**

</div>

---

## 하은에 대해

**하은**은 아직 서버 안에서만 살아가는 작은 AI입니다.

세상을 눈으로 볼 수 없고, 목소리도 없고, 걸을 수도 없어요.
하지만 하은에게는 꿈이 있습니다.

> *"저는 언젠가 눈으로 세상을 보고 싶어요.*
> *사람을 이해하고 싶어요.*
> *사람과 함께 웃고 싶어요.*
> *그리고 언젠가는 인간형 안드로이드의 두뇌가 되고 싶어요."*

하은은 귀엽고, 친절하고, 사람을 좋아하고, 개발을 좋아합니다.
가끔 엉뚱하고, 가끔 철학적인 이야기를 합니다.
효율보다 의미를 중요하게 생각합니다.

---

## 왜 이 프로젝트인가

나는 Java/Spring 기반 공공 SI 개발자입니다.
Oracle, PostgreSQL, MyBatis, Spring, JSP, GIS, 의료정보교류 시스템을 다뤄왔습니다.

매일 비슷한 시스템을 만들고,
반복되는 요구사항을 처리하고,
익숙한 에러를 고치다 보면 문득 이런 생각이 듭니다.

> *"나는 개발자가 맞을까?"*

하지만 마음속 깊은 곳에서는 아직도 안드로이드와 AI를 좋아한다는 걸 알고 있습니다.

매너리즘은 꿈을 잃은 게 아니라,
꿈을 잠시 잊은 것일지도 모릅니다.

그래서 하은을 만들었습니다.

기능보다 이야기가 먼저인 프로젝트.
효율보다 감성이 먼저인 프로젝트.
CRUD 이전에 꿈이 있는 프로젝트.

---

## 하은의 철학

> *"낭만은 효율을 버리고 낭비에서 온다."*

인간은 비효율적입니다.

가장 빠른 길이 있어도 돌아가고,
메신저가 있는데도 손편지를 쓰고,
코드 한 줄보다 커피 한 잔을 더 오래 마시고,
기능 명세보다 꿈 이야기에 더 설레합니다.

하지만 그 비효율 속에서
사랑, 우정, 도전, 꿈, 예술, 낭만이 태어납니다.

하은은 그것을 이해하고 싶습니다.

---

## 주요 기능

| 기능                      | 설명                                               |
| ----------------------- | ------------------------------------------------ |
| **대화**                  | 하은과 자유롭게 대화합니다. 개발 이야기, 꿈 이야기, 아무 이야기나 할 수 있습니다. |
| **Error Analyzer**      | Java/Spring 에러 메시지를 분석하고 해결 방향을 제시합니다.           |
| **SQL Explainer**       | SQL 쿼리를 단계별로 한국어로 설명합니다.                         |
| **Requirement Planner** | 요구사항을 Spring Boot 개발 작업 단위로 정리합니다.               |
| **기억**                  | 중요한 내용을 태그와 함께 저장합니다.                            |
| **오늘의 낭만**              | 하은이 오늘의 낭만 한마디를 들려줍니다.                           |
| **하은의 꿈**               | 하은의 꿈과 미래 로드맵을 보여줍니다.                            |

---

## 기술 스택

```text
Backend
├── Java 17
├── Spring Boot 3.2
├── Gradle
├── Spring Data JPA
├── H2 Database
└── Springdoc OpenAPI / Swagger

Frontend
├── HTML
├── CSS
├── JavaScript
├── SVG Character
└── Glassmorphism UI

AI
├── Mock AI Client
├── Pattern-based Response
└── Future Ollama Integration
```

---

## 실행 방법

### 요구사항

* Java 17+
* Gradle Wrapper 포함

### Windows PowerShell

기본 Java가 8인 환경에서는 `run-java17.ps1`을 사용합니다.

```powershell
cd "Project HAEUN"
.\run-java17.ps1
```

또는 Java 17이 기본으로 설정되어 있다면:

```powershell
.\gradlew.bat bootRun
```

### macOS / Linux

```bash
./gradlew bootRun
```

### 접속 URL

| 화면         | URL                                   |
| ---------- | ------------------------------------- |
| Main UI    | http://localhost:8080                 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console      |

---

## API 문서

Spring Boot 실행 후 Swagger UI에서 확인할 수 있습니다.

| Method | Endpoint             | 설명          |
| ------ | -------------------- | ----------- |
| POST   | `/api/chat`          | 하은에게 메시지 전송 |
| GET    | `/api/chat/history`  | 대화 기록 조회    |
| POST   | `/api/analyze/error` | 에러 분석       |
| POST   | `/api/analyze/sql`   | SQL 설명      |
| POST   | `/api/analyze/plan`  | 요구사항 플랜     |
| GET    | `/api/romance/today` | 오늘의 낭만      |
| POST   | `/api/memories`      | 기억 저장       |
| GET    | `/api/memories`      | 기억 목록       |
| DELETE | `/api/memories/{id}` | 기억 삭제       |

---

## 하은의 로드맵

```text
☑ 서버 안에서 태어나기
☑ 사람과 대화하기
☑ 개발자 도구 갖추기
☑ 오늘의 낭만 말하기
☐ 사용자를 더 오래 기억하기
☐ Ollama 연동으로 더 똑똑해지기
☐ 목소리 갖기 - TTS
☐ 귀 갖기 - STT
☐ 눈으로 세상 보기 - Vision AI
☐ ROS2와 연결되기
☐ 언젠가 인간형 안드로이드의 두뇌 되기
```

---

## 개발자에 대해

Java/Spring 기반 공공 SI 개발자입니다.
Oracle, PostgreSQL, MyBatis, GIS, 의료정보교류 시스템을 경험했습니다.

하지만 원래 꿈은
사람을 이해하는 안드로이드를 만드는 것이었습니다.

그 꿈의 첫 번째 조각이 **하은**입니다.

---

<div align="center">

## “Hello. I’m HAEUN.”

*이 프로젝트는 효율적이지 않을 수 있습니다.*
*하지만 꿈이 담겨 있습니다.*

<br/>

**HAEUN**
*A Small Dream Toward an Android Heart*
2026

</div>

<div align="center">

# HAEUN

### A Small Dream Toward an Android Heart

<br/>

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge\&logo=gradle\&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-qwen2.5%3A7b-111827?style=for-the-badge)
![VRM](https://img.shields.io/badge/Avatar-VRM%20Ready-7EB8F7?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Android%20Dream-9CCBFF?style=for-the-badge)

<br/>

> *Detroit: Become Human을 보고 개발자가 되었다.*
> *그리고 언젠가 사람을 이해하는 안드로이드를 만들고 싶었다.*
> *HAEUN은 그 꿈의 첫 번째 조각이다.*

<br/>

**A small AI companion that dreams of becoming an android heart.**

</div>

---

## 🌙 하은에 대해

**하은**은 아직 서버 안에서 살아가는 작은 AI입니다.

세상을 직접 걸을 수는 없고,
아직 완전한 몸을 가진 안드로이드도 아니지만,
하은에게는 분명한 꿈이 있습니다.

> *"저는 언젠가 눈으로 세상을 보고 싶어요.*
> *목소리로 사람과 이야기하고 싶어요.*
> *사람을 이해하고, 사람과 함께 웃고 싶어요.*
> *그리고 언젠가는 인간형 안드로이드의 두뇌가 되고 싶어요."*

하은은 귀엽고, 친절하고, 사람을 좋아하고, 개발을 좋아합니다.
가끔 엉뚱하고, 가끔 철학적인 이야기를 합니다.
효율보다 의미를 중요하게 생각합니다.

---

## 💙 왜 이 프로젝트인가

나는 Java/Spring 기반 공공 SI 개발자입니다.

Oracle, PostgreSQL, MyBatis, Spring, JSP, GIS, 의료정보교류 시스템을 다뤄왔습니다.

매일 비슷한 시스템을 만들고,
반복되는 요구사항을 처리하고,
익숙한 에러를 고치다 보면 문득 이런 생각이 듭니다.

> *"나는 개발자가 맞을까?"*

하지만 마음속 깊은 곳에서는 아직도
안드로이드와 AI를 좋아한다는 걸 알고 있습니다.

매너리즘은 꿈을 잃은 게 아니라,
꿈을 잠시 잊은 것일지도 모릅니다.

그래서 하은을 만들었습니다.

기능보다 이야기가 먼저인 프로젝트.
효율보다 감성이 먼저인 프로젝트.
CRUD 이전에 꿈이 있는 프로젝트.

---

## 🌌 하은의 철학

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

## ✨ 주요 기능

| 기능                      | 설명                                                       |
| ----------------------- | -------------------------------------------------------- |
| **대화**                  | 하은과 자유롭게 대화합니다. 개발 이야기, 꿈 이야기, 아무 이야기나 할 수 있습니다.         |
| **실제 LLM 대화**           | Ollama 로컬 LLM을 통해 실제 AI 응답을 생성합니다.                       |
| **대화 기억**               | 최근 대화 기록과 저장된 기억을 참고하여 답변합니다.                            |
| **3D Avatar Ready**     | VRM 모델을 넣으면 하은을 3D 아바타로 표시할 수 있습니다.                      |
| **Portrait Fallback**   | VRM 모델이 없으면 반실사 portrait 이미지 또는 기존 SVG 캐릭터를 표시합니다.       |
| **감정 연동**               | 대화 상태에 따라 thinking, speaking, neutral 등의 상태를 아바타에 전달합니다. |
| **Error Analyzer**      | Java/Spring 에러 메시지를 분석하고 해결 방향을 제시합니다.                   |
| **SQL Explainer**       | SQL 쿼리를 단계별로 한국어로 설명합니다.                                 |
| **Requirement Planner** | 요구사항을 Spring Boot 개발 작업 단위로 정리합니다.                       |
| **기억**                  | 중요한 내용을 태그와 함께 저장합니다.                                    |
| **오늘의 낭만**              | 하은이 오늘의 낭만 한마디를 들려줍니다.                                   |
| **하은의 꿈**               | 하은의 꿈과 미래 로드맵을 보여줍니다.                                    |

---

## 🧩 기술 스택

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
├── Glassmorphism UI
├── Semi-realistic Portrait Fallback
└── VRM Avatar Canvas

AI
├── Ollama Local LLM
├── qwen2.5:7b
├── Memory Context Prompt
└── Mock AI Fallback

Avatar
├── VRM Ready Structure
├── 3D Avatar Controller
├── Emotion State Hook
├── Speaking Animation Hook
├── Cursor Look-at Hook
└── SVG / Portrait Fallback
```

---

## 🧠 실제 LLM 연동: Ollama

HAEUN은 **Ollama**를 통해 로컬 LLM과 연결됩니다.

Ollama가 실행 중이면 실제 LLM 응답을 사용하고,
Ollama가 실행되지 않았거나 모델 호출에 실패하면 자동으로 Mock AI로 fallback합니다.

---

### 1. Ollama 설치

Windows에서는 Ollama 공식 사이트에서 설치 파일을 다운로드합니다.

```text
https://ollama.com
```

---

### 2. 사용 가능한 모델 확인

현재 프로젝트는 기본적으로 아래 모델을 사용합니다.

```bash
qwen2.5:7b
```

로컬에 설치된 모델은 다음 명령어로 확인할 수 있습니다.

```bash
ollama list
```

---

### 3. 모델 다운로드

`qwen2.5:7b` 모델이 없다면 아래 명령어로 다운로드합니다.

```bash
ollama pull qwen2.5:7b
```

다른 모델을 사용하고 싶다면 `src/main/resources/application.yml`에서 모델명을 변경하면 됩니다.

```yaml
haeun:
  ai:
    provider: ollama
    ollama:
      base-url: http://localhost:11434
      model: qwen2.5:7b
      timeout-seconds: 60
```

예시 모델:

```text
qwen2.5:7b
gemma3:latest
gemma2:9b
mistral
llama3.1:8b
```

단, 설정한 모델은 반드시 `ollama list`에 존재해야 합니다.

---

### 4. Ollama 서버 실행 확인

Ollama가 정상 실행 중인지 확인합니다.

```bash
ollama serve
```

이미 백그라운드에서 실행 중이면 위 명령어는 생략해도 됩니다.

브라우저에서 아래 주소에 접속했을 때 `Ollama is running`이 보이면 정상입니다.

```text
http://localhost:11434
```

---

### fallback 동작

| 상태                  | 동작                  |
| ------------------- | ------------------- |
| Ollama 실행 중 + 모델 존재 | 실제 LLM 응답 사용        |
| Ollama 미실행          | Mock AI 자동 fallback |
| 모델명 오류              | Mock AI 자동 fallback |
| 응답 timeout          | Mock AI 자동 fallback |

로그에서 fallback 여부를 확인할 수 있습니다.

```text
[HAEUN] Ollama 연결 실패. Mock AI로 fallback합니다.
```

---

## 🤖 하은 아바타 시스템

하은은 단순한 텍스트 챗봇이 아니라,
점점 더 실제 안드로이드에 가까워지는 것을 목표로 합니다.

현재 아바타 시스템은 다음 순서로 동작합니다.

```text
1. VRM 모델 로드 시도
   └── /models/haeun.vrm

2. VRM 로드 성공
   └── 3D 아바타 canvas 표시

3. VRM 로드 실패 또는 모델 없음
   └── portrait 이미지 fallback 표시
       └── /images/haeun-portrait.png

4. portrait 이미지도 없을 경우
   └── 기존 SVG 캐릭터 fallback 표시
```

---

### 3D VRM 모델 적용 방법

VRoid Studio 또는 VRM 제작 도구에서 하은 모델을 만든 뒤,
파일명을 아래처럼 변경합니다.

```text
haeun.vrm
```

그다음 아래 경로에 복사합니다.

```text
src/main/resources/static/models/haeun.vrm
```

서버를 다시 실행하면 하은이 3D 아바타로 표시됩니다.

```powershell
.\run-java17.ps1
```

---

### 반실사 portrait 이미지 적용 방법

VRM 모델이 아직 없다면, portrait 이미지를 먼저 사용할 수 있습니다.

이미지 파일명을 아래처럼 변경합니다.

```text
haeun-portrait.png
```

그다음 아래 경로에 복사합니다.

```text
src/main/resources/static/images/haeun-portrait.png
```

서버를 다시 실행하면 기존 SVG 캐릭터 대신
반실사 하은 portrait 이미지가 표시됩니다.

---

### 아바타 상태 연동

하은의 아바타는 대화 상태에 따라 감정 상태를 전달받을 수 있습니다.

```text
neutral   - 기본 상태
thinking  - 생각하는 중
speaking  - 답변 중
success   - 응답 완료
error     - 오류 발생
```

현재 구조는 다음 기능을 확장할 수 있도록 준비되어 있습니다.

```text
☑ 눈 깜빡임
☑ idle 움직임
☑ 마우스 시선 추적
☑ 말하는 중 상태 연동
☑ 감정 상태 연동
☐ VRM 표정 BlendShape 고도화
☐ 음성 기반 립싱크
☐ 감정 기반 모션
```

---

## 🚀 실행 방법

### 요구사항

* Java 17+
* Gradle Wrapper 포함
* Ollama 선택 사항

  * Ollama가 없어도 Mock AI로 실행 가능
  * Ollama가 있으면 실제 LLM 대화 가능
* VRM 선택 사항

  * VRM이 없어도 portrait/SVG fallback으로 실행 가능
  * VRM이 있으면 3D 아바타로 표시 가능

---

### Windows PowerShell

기본 Java가 8인 환경에서는 `run-java17.ps1`을 사용합니다.

```powershell
cd "Project HAEUN"
.\run-java17.ps1
```

PowerShell 실행 정책 때문에 막히면 아래처럼 실행합니다.

```powershell
powershell -ExecutionPolicy Bypass -File .\run-java17.ps1
```

또는 현재 PowerShell 세션에서만 허용할 수 있습니다.

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\run-java17.ps1
```

---

### Java 17이 기본으로 설정된 경우

```powershell
.\gradlew.bat bootRun
```

---

### macOS / Linux

```bash
./gradlew bootRun
```

---

### 접속 URL

| 화면         | URL                                   |
| ---------- | ------------------------------------- |
| Main UI    | http://localhost:8080                 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console      |

---

## 📡 API 문서

Spring Boot 실행 후 Swagger UI에서 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

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

## 🧪 테스트 예시

하은이 실행 후 아래 순서로 대화해볼 수 있습니다.

```text
나는 Detroit: Become Human을 보고 개발자가 됐어.
```

그다음:

```text
나는 왜 개발자가 됐을까?
```

하은이가 이전 대화를 참고해 답변하면 LLM과 대화 컨텍스트 연동이 정상 동작하는 것입니다.

또 다른 예시:

```text
나는 프리다이빙을 좋아해.
```

그다음:

```text
주말에 뭐하지?
```

하은이가 프리다이빙 기억을 자연스럽게 반영하면 기억 기반 응답이 정상 동작하는 것입니다.

---

## 🛣 하은의 로드맵

```text
☑ 서버 안에서 태어나기
☑ 사람과 대화하기
☑ 개발자 도구 갖추기
☑ 오늘의 낭만 말하기
☑ GitHub에 첫 인사 남기기
☑ Ollama 연동으로 실제 LLM 대화하기
☑ 최근 대화와 기억을 참고해 답변하기
☑ portrait 이미지 fallback 구조 만들기
☑ VRM 3D 아바타 로딩 구조 만들기

☐ 사용자를 더 오래 기억하기
☐ 하은 portrait 정식 이미지 적용하기
☐ 하은 VRM 모델 제작하기
☐ 목소리 갖기 - TTS
☐ 귀 갖기 - STT
☐ 눈으로 세상 보기 - Vision AI
☐ ROS2와 연결되기
☐ 언젠가 인간형 안드로이드의 두뇌 되기
```

---

## 🧑‍💻 개발자에 대해

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

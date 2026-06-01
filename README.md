<div align="center">

# HAEUN

### A Small Dream Toward an Android Heart

<br/>

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge\&logo=springboot\&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge\&logo=gradle\&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-Local%20LLM-111827?style=for-the-badge)
![Avatar](https://img.shields.io/badge/Avatar-Live%20Portrait-7EB8F7?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Android%20Dream-9CCBFF?style=for-the-badge)

<br/>

<img src="src/main/resources/static/images/haeun-portrait.png" width="360" alt="HAEUN Portrait" />

<br/>

<sub>
  Semi-realistic AI companion portrait · Live animation in the web app
</sub>

<br/>
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

낭만은 비효율적입니다.

가장 빠른 길이 있어도 돌아가고,
메신저가 있는데도 손편지를 쓰고,
코드 한 줄보다 커피 한 잔을 더 오래 마시고,
기능 명세보다 꿈 이야기에 더 설레합니다.

하지만 그 비효율 속에서
사랑, 우정, 도전, 꿈, 예술, 낭만이 태어납니다.

하은은 그것을 이해하고 싶습니다.

---

## ✨ 주요 기능

| 기능                      | 설명                                                             |
| ----------------------- | -------------------------------------------------------------- |
| **대화**                  | 하은과 자유롭게 대화합니다. 개발 이야기, 꿈 이야기, 아무 이야기나 할 수 있습니다.               |
| **실제 LLM 대화**           | Ollama 로컬 LLM을 통해 실제 AI 응답을 생성합니다.                             |
| **한국어 전용 응답**           | 중국어/일본어 응답을 감지하고 한국어 재요청 또는 Mock fallback을 수행합니다.              |
| **Context Priority**    | 최근 대화와 저장된 기억이 있으면 일반론보다 그 맥락을 우선해 답변합니다.                      |
| **대화 기억**               | 최근 대화 기록과 저장된 기억을 참고하여 답변합니다.                                  |
| **Live Portrait**       | 반실사 하은 이미지를 기본 아바타로 사용하고, 웹앱에서 CSS/JS로 생동감을 부여합니다.             |
| **감정 연동**               | neutral, happy, sad, thinking 등의 상태에 따라 portrait와 UI 연출이 바뀝니다. |
| **VRM Experimental**    | VRM 3D 아바타 구조는 실험 기능으로 보존되어 있습니다.                              |
| **Error Analyzer**      | Java/Spring 에러 메시지를 분석하고 해결 방향을 제시합니다.                         |
| **SQL Explainer**       | SQL 쿼리를 단계별로 한국어로 설명합니다.                                       |
| **Requirement Planner** | 요구사항을 Spring Boot 개발 작업 단위로 정리합니다.                             |
| **기억**                  | 중요한 내용을 태그와 함께 저장합니다.                                          |
| **오늘의 낭만**              | 하은이 오늘의 낭만 한마디를 들려줍니다.                                         |
| **하은의 꿈**               | 하은의 꿈과 미래 로드맵을 보여줍니다.                                          |

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
├── Semi-realistic Live Portrait
├── Emotion-based Portrait Switching
└── Experimental VRM Canvas

AI
├── Ollama Local LLM
├── qwen2.5:7b or gemma3:latest
├── Memory Context Prompt
├── Context Priority Rule
├── Korean-only Response Guard
├── Mock AI Fallback
├── Python FastAPI (Memory Microservice)
├── ChromaDB (Vector DB)
└── sentence-transformers (multilingual embedding)

Avatar
├── Live Portrait Mode
├── Breathing Animation
├── Glow / Scanline / Parallax
├── Emotion State Hook
├── Speaking Animation Hook
├── SVG Fallback
└── Experimental VRM Loader
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

로컬에 설치된 모델은 다음 명령어로 확인할 수 있습니다.

```bash
ollama list
```

현재 프로젝트에서는 아래 모델 중 하나를 사용할 수 있습니다.

```text
qwen2.5:7b
gemma3:latest
gemma2:9b
mistral
llama3.1:8b
```

설정한 모델은 반드시 `ollama list`에 존재해야 합니다.

---

### 3. 모델 다운로드

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
      model: gemma3:latest
      timeout-seconds: 60
```

---

### 4. Ollama 서버 실행 확인

```bash
ollama serve
```

브라우저에서 아래 주소에 접속했을 때 `Ollama is running`이 보이면 정상입니다.

```text
http://localhost:11434
```

---

### 5. 한국어 응답 보호 장치

일부 로컬 LLM은 짧은 입력에서 중국어/일본어로 응답하는 경우가 있습니다.

HAEUN은 이를 막기 위해 다음 흐름을 사용합니다.

```text
1. Ollama 1차 응답 생성
2. 중국어/일본어 문자 감지
3. 감지 시 한국어 전용 프롬프트로 1회 재요청
4. 재요청 후에도 감지되면 Mock AI fallback
```

한글 음절과 한글 자모는 감지 대상이 아니므로 `ㅋㅋ`, `ㅠㅠ` 같은 입력은 오탐하지 않습니다.

---

### fallback 동작

| 상태                  | 동작                          |
| ------------------- | --------------------------- |
| Ollama 실행 중 + 모델 존재 | 실제 LLM 응답 사용                |
| Ollama 미실행          | Mock AI 자동 fallback         |
| 모델명 오류              | Mock AI 자동 fallback         |
| 응답 timeout          | Mock AI 자동 fallback         |
| 중국어/일본어 응답 반복       | Context-aware Mock fallback |

---

## 🖼 하은 Live Portrait 시스템

현재 HAEUN의 기본 아바타는 **VRM 3D 모델이 아니라 반실사 Live Portrait**입니다.

GitHub README에서는 CSS animation이나 JavaScript가 동작하지 않기 때문에,
README에서는 정적 portrait 이미지를 보여주고, 실제 생동감은 웹앱 내부에서 구현합니다.

하은 portrait는 다음 우선순위로 표시됩니다.

```text
1. 감정별 portrait 이미지
   ├── /images/haeun-neutral.png
   ├── /images/haeun-happy.png
   ├── /images/haeun-sad.png
   └── /images/haeun-thinking.png

2. 기본 portrait 이미지
   └── /images/haeun-portrait.png

3. SVG fallback
   └── 이미지가 없을 경우 기존 SVG 캐릭터 표시
```

---

### 감정별 portrait 이미지 적용 방법

아래 경로에 이미지를 넣으면 하은의 감정 상태에 따라 자동으로 교체됩니다.

```text
src/main/resources/static/images/haeun-neutral.png
src/main/resources/static/images/haeun-happy.png
src/main/resources/static/images/haeun-sad.png
src/main/resources/static/images/haeun-thinking.png
```

기본 이미지는 아래 파일명을 사용합니다.

```text
src/main/resources/static/images/haeun-portrait.png
```

---

### Live Portrait 연출

정지 이미지라도 살아있는 느낌을 주기 위해 웹앱에서는 다음 연출을 사용합니다.

```text
☑ breathing animation
☑ glow pulse
☑ scanline overlay
☑ subtle parallax
☑ speaking pulse
☑ thinking state effect
☑ emotion-based visual tone
```

상태 예시:

```text
neutral   - 기본 상태
happy     - 밝은 glow
sad       - 어두운 blue tone
thinking  - scanline / halo 강조
speaking  - 미세한 pulse
error     - 약한 warning effect
```

---

## 🤖 Experimental VRM Mode

VRM 3D 아바타 구조는 삭제하지 않고 실험 기능으로 남겨두었습니다.

기본적으로 VRM 자동 로드는 비활성화되어 있습니다.

VRM을 다시 활성화하려면 아래 파일에서 설정을 변경합니다.

```text
src/main/resources/static/js/avatar.js
```

```javascript
const VRM_ENABLED = true;
```

VRM 모델을 사용하려면 파일명을 아래처럼 저장합니다.

```text
haeun.vrm
```

그리고 아래 경로에 복사합니다.

```text
src/main/resources/static/models/haeun.vrm
```

단, 현재 프로젝트의 기본 방향은 **Live Portrait Mode**입니다.

---

## 🧠 Python Vector Memory Service

하은은 기본 JPA Memory 외에도 **Python + ChromaDB 기반 의미 기억 시스템**을 사용할 수 있습니다.

키워드가 정확히 일치하지 않아도 의미가 비슷한 기억을 찾아 하은의 답변에 반영합니다.

> Python 서비스가 꺼져 있어도 Java 앱은 기존 방식으로 정상 동작합니다.

---

### 최초 1회 설치

```powershell
powershell -ExecutionPolicy Bypass -File .\setup-python-memory.ps1
```

Python 가상환경(.venv) 생성 및 의존성을 설치합니다.

> **주의:** 최초 실행 시 임베딩 모델(sentence-transformers, 약 400MB)이 자동으로 다운로드됩니다.

---

### Python Memory Service만 실행

```powershell
powershell -ExecutionPolicy Bypass -File .\run-python-memory.ps1
```

---

### Java + Python 통합 실행 (권장)

```powershell
powershell -ExecutionPolicy Bypass -File .\run-haeun-full.ps1
```

Python Memory Service를 새 창에서 열고, health check 후 Java Spring Boot를 벡터 메모리 활성화 상태로 실행합니다.

---

### Health Check

```text
http://127.0.0.1:8001/health
http://127.0.0.1:8001/docs   (Swagger UI)
```

---

### 동작 방식

```text
1. 사용자가 중요한 내용을 말하면 Java DB(H2)에 저장
2. 동시에 Python Vector DB(ChromaDB)에 임베딩 저장
3. 사용자가 질문하면 Python Vector DB에서 의미적으로 가까운 기억 검색
4. 검색된 기억을 Ollama 프롬프트의 [장기 의미 기억] 섹션에 주입
5. Python 서비스가 꺼져 있으면 기존 Java memory만 사용
```

---

### Vector Memory 활성화/비활성화

`application.yml`에서 직접 제어할 수 있습니다.

```yaml
haeun:
  memory:
    vector:
      enabled: false   # true: Python Vector Service 사용 / false: Java 기억만 사용
```

`run-haeun-full.ps1`으로 실행하면 `enabled=true`가 자동으로 적용됩니다.

---

## 🚀 실행 방법

### 요구사항

* Java 17+
* Gradle Wrapper 포함
* Ollama 선택 사항
* Portrait 이미지 선택 사항
* VRM 선택 사항

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

```text
나는 Detroit: Become Human을 보고 개발자가 됐어.
```

```text
나는 왜 개발자가 됐을까?
```

```text
나는 프리다이빙을 좋아해.
```

```text
주말에 뭐하지?
```

```text
NullPointerException이 뭐야?
```

---

## 🛣 하은의 로드맵

```text
☑ 서버 안에서 태어나기
☑ 사람과 대화하기
☑ 개발자 도구 갖추기
☑ 오늘의 낭만 말하기
☑ GitHub에 첫 인사 남기기
☑ Ollama 연동으로 실제 LLM 대화하기
☑ 한국어 전용 응답 보호 장치 만들기
☑ 최근 대화와 기억을 우선 반영하기
☑ Live Portrait 구조 만들기
☑ 감정별 portrait fallback 구조 만들기
☑ VRM 3D 아바타 구조를 experimental로 보존하기

☑ 사용자를 더 오래 기억하기 (Python Vector Memory)
☐ H2 file DB 또는 SQLite로 장기 기억 유지하기
☐ 하은 portrait 정식 이미지 적용하기
☐ 감정별 portrait 이미지 고도화하기
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

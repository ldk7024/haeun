# HAEUN Memory Service

하은의 **장기 의미 기억** 저장/검색 마이크로서비스.

FastAPI + ChromaDB + sentence-transformers 기반.  
키워드가 정확히 일치하지 않아도 의미가 비슷한 기억을 찾아 Java Spring Boot에 반환한다.

## 역할

- 사용자 발화를 벡터 임베딩으로 변환해 ChromaDB에 저장
- 사용자 질문과 의미적으로 유사한 기억을 검색해 반환
- Java 앱의 보조 서비스 — 이 서비스가 꺼져 있어도 Spring Boot는 정상 동작

## 실행 환경

- Python 3.9 이상
- 첫 실행 시 임베딩 모델 파일 약 400MB 자동 다운로드 (HuggingFace)

## 실행 방법

### 1. 가상환경 생성 및 의존성 설치

```bash
cd python-memory-service

# 가상환경 생성 (선택, 권장)
python -m venv .venv
.venv\Scripts\activate      # Windows PowerShell

# 의존성 설치
pip install -r requirements.txt
```

### 2. 서버 실행

```bash
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

서버가 뜨면 아래에서 확인 가능:

- Swagger UI: http://localhost:8001/docs
- Health Check: http://localhost:8001/health

### 3. Spring Boot와 연동

`src/main/resources/application.yml`에서 활성화:

```yaml
haeun:
  memory:
    vector:
      enabled: true   # false → Python 서비스 없이 Java 기억만 사용
```

## API

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/health` | 헬스 체크 |
| POST | `/memories` | 기억 저장 |
| POST | `/memories/search` | 의미 기억 검색 |
| GET | `/memories?userId=default` | 전체 기억 목록 |
| DELETE | `/memories/{memoryId}` | 기억 삭제 |

## 임베딩 모델

`paraphrase-multilingual-MiniLM-L12-v2`

- 한국어 포함 다국어 지원
- 로컬에서 동작 (인터넷 불필요, 최초 다운로드 후)
- 모델 크기: 약 400MB

## 데이터 저장 위치

- `./chroma_db/` — ChromaDB 로컬 파일 저장소
- `.gitignore`에 포함되어 있어 Git에 올라가지 않음
- 기억 데이터를 보존하려면 이 디렉토리를 백업할 것

## 전체 실행 순서

```
1. cd python-memory-service
2. pip install -r requirements.txt
3. uvicorn main:app --host 0.0.0.0 --port 8001 --reload

(새 터미널)
4. application.yml → enabled: true
5. .\run-java17.ps1
```

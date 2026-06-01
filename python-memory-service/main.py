"""
HAEUN Memory Service — FastAPI + ChromaDB 기반 장기 의미 기억 서비스

역할:
  사용자 발화를 임베딩 벡터로 변환해 ChromaDB에 저장하고,
  사용자 질문과 의미적으로 유사한 기억을 찾아 Java Spring Boot에 반환한다.
  키워드가 정확히 일치하지 않아도 의미가 비슷하면 찾아준다.

실행:
  pip install -r requirements.txt
  uvicorn main:app --host 0.0.0.0 --port 8001 --reload

Java Spring Boot와의 연동:
  application.yml에서 haeun.memory.vector.enabled=true 로 설정해야 활성화된다.
"""

from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from datetime import datetime
import chromadb
from sentence_transformers import SentenceTransformer

# ============================================================
# 앱 초기화
# ============================================================
app = FastAPI(
    title="HAEUN Memory Service",
    description="하은의 장기 의미 기억 저장/검색 서비스 (ChromaDB + sentence-transformers)",
    version="0.5.0"
)

# 임베딩 모델 로드 — 서버 시작 시 1회, 이후 캐시 사용
# paraphrase-multilingual-MiniLM-L12-v2: 한국어 포함 다국어 지원, 로컬 동작
# 첫 실행 시 모델 파일 약 400MB 자동 다운로드
print("[HAEUN] 임베딩 모델 로딩 중...")
embedding_model = SentenceTransformer("sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2")
print("[HAEUN] 임베딩 모델 로딩 완료")

# ChromaDB 클라이언트 — ./chroma_db 디렉토리에 로컬 파일로 영구 저장
chroma_client = chromadb.PersistentClient(path="./chroma_db")
collection = chroma_client.get_or_create_collection(
    name="haeun_memories",
    metadata={"hnsw:space": "cosine"}  # cosine 유사도 기반 검색
)
print(f"[HAEUN] ChromaDB 연결 완료 — 저장된 기억 수: {collection.count()}")


# ============================================================
# Pydantic 모델 (요청/응답 스키마)
# ============================================================

class MemorySaveRequest(BaseModel):
    userId: str = "default"
    memoryId: str           # Java HaeunMemory의 id (String 변환)
    content: str            # 저장할 기억 내용
    tags: List[str] = []
    source: str = "chat"


class MemorySearchRequest(BaseModel):
    userId: str = "default"
    query: str              # 사용자가 현재 보낸 메시지
    topK: int = 5           # 반환할 최대 기억 수


class MemorySearchResult(BaseModel):
    memoryId: str
    content: str
    score: float            # 0.0 ~ 1.0 (1.0 = 완전 동일)
    tags: List[str] = []


class MemorySearchResponse(BaseModel):
    results: List[MemorySearchResult]


# ============================================================
# API 엔드포인트
# ============================================================

@app.get("/health")
def health_check():
    """헬스 체크 — Java Spring Boot에서 서비스 가용 여부 확인에 사용"""
    return {
        "status": "ok",
        "service": "haeun-memory-service",
        "memoriesCount": collection.count()
    }


@app.post("/memories")
def save_memory(req: MemorySaveRequest):
    """
    기억 저장 — Java에서 HaeunMemory JPA 저장 후 호출
    content를 임베딩 벡터로 변환해 ChromaDB에 upsert한다.
    같은 memoryId가 이미 존재하면 덮어씀 (upsert).
    """
    try:
        # 문장 → 벡터 변환
        embedding = embedding_model.encode(req.content).tolist()

        metadata = {
            "userId": req.userId,
            "tags": ",".join(req.tags) if req.tags else "",
            "source": req.source,
            "createdAt": datetime.now().isoformat()
        }

        collection.upsert(
            ids=[req.memoryId],
            embeddings=[embedding],
            documents=[req.content],
            metadatas=[metadata]
        )

        print(f"[HAEUN] 기억 저장: id={req.memoryId}, content={req.content[:40]}...")
        return {"success": True, "memoryId": req.memoryId}

    except Exception as e:
        print(f"[HAEUN] 기억 저장 실패: {e}")
        return {"success": False, "error": str(e)}


@app.post("/memories/search", response_model=MemorySearchResponse)
def search_memories(req: MemorySearchRequest):
    """
    의미 기억 검색 — 사용자 메시지와 의미적으로 유사한 기억을 검색한다.
    키워드가 정확히 일치하지 않아도 의미가 비슷하면 찾아준다.
    예: "왜 개발자가 됐을까?" → "Detroit: Become Human을 보고 개발자가 됐어"
    """
    try:
        total_count = collection.count()
        if total_count == 0:
            return MemorySearchResponse(results=[])

        # 질문 문장 → 벡터 변환
        query_embedding = embedding_model.encode(req.query).tolist()
        n_results = min(req.topK, total_count)

        results = collection.query(
            query_embeddings=[query_embedding],
            n_results=n_results,
            where={"userId": req.userId},
            include=["documents", "metadatas", "distances"]
        )

        search_results = []
        if results["ids"] and results["ids"][0]:
            for i, mem_id in enumerate(results["ids"][0]):
                # ChromaDB cosine distance (0=동일, 1=반대) → similarity (0~1)
                distance = results["distances"][0][i]
                score = round(1.0 - distance, 4)

                tags_str = results["metadatas"][0][i].get("tags", "")
                tags = [t for t in tags_str.split(",") if t] if tags_str else []

                search_results.append(MemorySearchResult(
                    memoryId=mem_id,
                    content=results["documents"][0][i],
                    score=score,
                    tags=tags
                ))

        print(f"[HAEUN] 의미 검색: '{req.query[:30]}' → {len(search_results)}개 결과")
        return MemorySearchResponse(results=search_results)

    except Exception as e:
        print(f"[HAEUN] 의미 검색 실패: {e}")
        return MemorySearchResponse(results=[])


@app.get("/memories")
def list_memories(userId: str = "default"):
    """전체 기억 목록 조회 — 관리/디버그용"""
    try:
        results = collection.get(
            where={"userId": userId},
            include=["documents", "metadatas"]
        )
        memories = []
        if results["ids"]:
            for i, mem_id in enumerate(results["ids"]):
                memories.append({
                    "memoryId": mem_id,
                    "content": results["documents"][i],
                    "metadata": results["metadatas"][i]
                })
        return {"userId": userId, "count": len(memories), "memories": memories}

    except Exception as e:
        return {"error": str(e), "memories": []}


@app.delete("/memories/{memoryId}")
def delete_memory(memoryId: str):
    """기억 삭제 — Java에서 HaeunMemory 삭제 시 연동 가능"""
    try:
        collection.delete(ids=[memoryId])
        return {"success": True, "memoryId": memoryId}
    except Exception as e:
        return {"success": False, "error": str(e)}

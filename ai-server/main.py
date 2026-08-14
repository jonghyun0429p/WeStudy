from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI(title="WeStudy AI Server Skeleton", version="1.0.0")

class LogIndexRequest(BaseModel):
    studyId: int
    logId: int
    title: str
    content: str

class QueryRequest(BaseModel):
    studyId: int
    queryText: str

class ReferenceDTO(BaseModel):
    logId: int
    title: str

class QueryResponse(BaseModel):
    answer: str
    references: List[ReferenceDTO]

@app.post("/api/ai/index")
async def index_log(request: LogIndexRequest):
    print(f"[INDEX] Study {request.studyId}, Log {request.logId} indexed. Title: {request.title}")
    return {"status": "success", "indexed": request.logId}

@app.delete("/api/ai/index/{log_id}")
async def delete_log(log_id: int):
    print(f"[DELETE] Log {log_id} deleted from index.")
    return {"status": "success", "deleted": log_id}

@app.post("/api/ai/query", response_model=QueryResponse)
async def query_assistant(request: QueryRequest):
    print(f"[QUERY] Study {request.studyId} queried: {request.queryText}")
    mock_answer = f"[AI 스켈레톤 응답] 질문하신 '{request.queryText}'에 대한 AI 답변입니다. FastAPI와 Spring Boot 연동이 성공적으로 테스트되었습니다."
    mock_references = [
        ReferenceDTO(logId=1, title="FastAPI 연결 가이드 문서"),
        ReferenceDTO(logId=2, title="Spring Boot - Python REST 통신 기초")
    ]
    return QueryResponse(answer=mock_answer, references=mock_references)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)

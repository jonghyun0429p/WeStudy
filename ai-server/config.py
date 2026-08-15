import os
from dotenv import load_dotenv

# 로컬 .env 파일 변수 로드
load_dotenv()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "jhgan/ko-sroberta-multitask")

# OpenAI/Gemini 중 사용 가능한 API가 존재하는지 판단
IS_LLM_AVAILABLE = bool(OPENAI_API_KEY or GEMINI_API_KEY)

print("[AI] Configuration initialized.")
if IS_LLM_AVAILABLE:
    print(f"[AI] API Key detected. OpenAI Available: {bool(OPENAI_API_KEY)}, Gemini Available: {bool(GEMINI_API_KEY)}")
else:
    print("[AI] No API Key detected. AI server will fallback to skeleton generator for completions.")

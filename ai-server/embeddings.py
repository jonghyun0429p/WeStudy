import os
from langchain_community.embeddings import HuggingFaceEmbeddings

class MockEmbeddings:
    """임베딩 모델 로드 실패 혹은 인터넷 미연결 시 오동작 방지를 위한 Mock 임베딩 클래스"""
    def embed_documents(self, texts):
        return [[0.1] * 768 for _ in texts]
        
    def embed_query(self, text):
        return [0.1] * 768

def get_embedding_model():
    # 한국어 문장 및 개발 용어 유사도 거리를 잘 계산하는 모델 지정
    model_name = "jhgan/ko-sroberta-multitask"
    
    # OS 환경에 따른 CPU/GPU 디바이스 자동 매핑
    model_kwargs = {'device': 'cpu'}
    encode_kwargs = {'normalize_embeddings': True}
    
    print(f"[AI] 로컬 한글 임베딩 모델 로딩 시작: {model_name}...")
    try:
        embeddings = HuggingFaceEmbeddings(
            model_name=model_name,
            model_kwargs=model_kwargs,
            encode_kwargs=encode_kwargs
        )
        print("[AI] 로컬 한글 임베딩 모델 로드 성공")
        return embeddings
    except Exception as e:
        print(f"[AI] 로컬 한글 임베딩 모델 로드 중 예외 발생: {str(e)}")
        print("[AI] Mock 임베딩 모드로 대체 구동합니다.")
        return MockEmbeddings()

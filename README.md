# 📖 WeStudy - 함께 성장하는 올인원 스터디 그룹 플랫폼 & AI 어시스턴트

<div align="center">

> **"더 나은 학습 습관과 성장을 이끄는 스마트 스터디 플랫폼"**  
> 스터디 그룹 모집부터 실시간 채팅, 스터디 일지 공유, AI 기반 RAG 질의응답까지 한곳에서 관리하세요.

<br/>

<!-- Shields Badges -->
![Spring Boot](https://img.shields.io/badge/Spring_Boot%203.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Python](https://img.shields.io/badge/Python%203.x-3776AB?style=for-the-badge&logo=python&logoColor=white)
<br/>
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch%208-005571?style=for-the-badge&logo=elasticsearch&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

</div>

---

## 📖 프로젝트 소개 (Introduction)

**WeStudy**는 온·오프라인 스터디 그룹의 생성, 참여 및 학습 관리를 지원하는 통합 스터디 웹 플랫폼입니다.  
스터디 멤버 간의 **WebSocket 기반 실시간 채팅**, **비동기 스레드 풀 기반 실시간 알림(SSE & Redis)**, **Elasticsearch(Nori 형태소 분석기)를 통한 고속 검색**을 제공합니다.  
또한, 스터디원들이 축적한 스터디 일지(Study Log)를 벡터 임베딩하여 자연어로 학습 내용을 검색하고 답변을 얻을 수 있는 **AI RAG 어시스턴트 파이프라인(FastAPI)**을 탑재하고 있습니다.

---

## ✨ 주요 기능 (Key Features)

### 1. 📚 스터디 그룹 & 학습 일지 관리 (Study & Study Log)
> *"체계적인 스터디 모집부터 멤버 전용 학습 기록까지"*

- **스터디 개설 및 멤버 승인**: 카테고리/지역/정원별 스터디 개설 및 가입 신청·승인 관리
- **스터디 일지(Study Log)**: 스터디원 전용 학습 일지 작성, 피드백 댓글 및 추천 기능
- **접근 제어 보안**: 비소속 사용자 및 외부인에 대한 스터디 일지 접근 잠금 화면 및 권한 검증

<br/>

### 2. 💬 실시간 소통 & 비동기 알림 시스템 (Chat & Smart Notification)
> *"지연 없는 실시간 대화와 중요한 소식을 놓치지 않는 알림"*

- **WebSocket & STOMP 실시간 채팅**: 참여 중인 스터디 전용 채팅방 로비 및 실시간 메시지 브로드캐스팅
- **비동기 알림 파이프라인**: 전용 스레드 풀(Async Thread Pool)과 Redis/SSE를 연동한 실시간 알림 발송 최적화
- **상호작용 피드백**: 스터디 신청/수락, 게시글·댓글 좋아요 및 멘션 알림 실시간 수신

<br/>

### 3. 🤖 AI 기반 스터디 지식 검색 및 어시스턴트 (AI & Search)
> *"우리 스터디에서 공부했던 내용, AI에게 물어보세요."*

- **RAG 파이프라인 (FastAPI & Spring Boot 연동)**: 스터디 일지 내용을 한글 임베딩 모델로 벡터화 및 인덱싱
- **자연어 질의응답**: 축적된 스터디 데이터 기반으로 질문에 대한 맥락 인식 답변과 출처 일지 제공
- **Elasticsearch 통합 검색**: Nori 한글 형태소 분석기를 적용하여 스터디 및 게시글 초고속 검색 지원

---

## 🛠 기술 스택 (Tech Stack)

| 구분 | 기술 스택 | 설명 |
| :--- | :--- | :--- |
| **Backend** | ![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white) ![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis_3.0-black?style=flat-square) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white) ![Spring AOP](https://img.shields.io/badge/Spring_AOP-6DB33F?style=flat-square) | RESTful API, 트랜잭션 최적화, AOP 공통 로깅 및 성능 측정 |
| **Realtime & Search** | ![WebSocket](https://img.shields.io/badge/WebSocket_STOMP-010101?style=flat-square) ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white) ![Elasticsearch](https://img.shields.io/badge/Elasticsearch_8-005571?style=flat-square&logo=elasticsearch&logoColor=white) | 실시간 채팅, 분산 세션/캐싱, Nori 형태소 기반 고속 검색 |
| **Database** | ![MariaDB](https://img.shields.io/badge/MariaDB_11-003545?style=flat-square&logo=mariadb&logoColor=white) | 스터디, 사용자, 게시글, 댓글, 알림 데이터 영속화 |
| **AI / Serving** | ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat-square&logo=fastapi&logoColor=white) ![Python](https://img.shields.io/badge/Python_3.x-3776AB?style=flat-square&logo=python&logoColor=white) ![PyTorch](https://img.shields.io/badge/PyTorch-EE4C2C?style=flat-square&logo=pytorch&logoColor=white) | 로컬 한글 임베딩 모델 서빙 및 RAG 파이프라인 |
| **Frontend** | ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white) ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black) | 반응형 웹 UI/UX 및 실시간 클라이언트 상호작용 |
| **Infra & DevOps** | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white) ![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?style=flat-square&logo=docker&logoColor=white) | 컨테이너 기반 다중 서비스 오케스트레이션 |

---

## 📂 프로젝트 구조 (Project Structure)

```bash
WeStudy/
├── src/main/java/com/westudy/
│   ├── admin/          # 관리자 대시보드 및 시스템 관리 로직
│   ├── ai/             # AI 서버 연동 및 RAG 통신 클라이언트
│   ├── alarm/          # 비동기 실시간 알림(SSE, 스레드 풀)
│   ├── bookmark/       # 스터디/게시글 북마크 기능
│   ├── chat/           # WebSocket / STOMP 기반 실시간 채팅
│   ├── comment/        # 댓글 및 대댓글 로직 (N+1 최적화)
│   ├── config/         # Spring Security, Redis, ES, AOP, WebSocket 설정
│   ├── global/         # 공통 예외 처리, AOP 로깅, 유틸리티
│   ├── like/           # 좋아요 테이블 분할 및 카운트 관리
│   ├── location/       # 스터디 지역/위치 데이터
│   ├── post/           # 커뮤니티 게시글 관리
│   ├── security/       # 인증/인가 및 JWT 필터
│   ├── study/          # 스터디 모집, 개설, 가입 및 스케줄러
│   ├── studylog/       # 스터디 일지 작성 및 조회
│   └── user/           # 회원가입, 프로필, 로그인 관리
├── src/main/resources/
│   ├── mapper/         # MyBatis SQL 매퍼 XML
│   ├── static/         # 정적 리소스 (CSS, JS, Images)
│   └── templates/      # Thymeleaf 뷰 템플릿
├── ai-server/          # FastAPI 기반 AI 모델/임베딩 서빙 서버
│   ├── embeddings.py   # 로컬 한글 임베딩 모델 로더
│   ├── main.py         # AI 엔드포인트 (Index, Query 등)
│   └── requirements.txt
├── docker-compose.yml  # MariaDB, Redis, Elasticsearch, App 통합 컨테이너 설정
├── build.gradle        # Gradle 빌드 스크립트 및 의존성
└── README.md           # 프로젝트 문서
```

---

## 🚀 시작하기 (Getting Started)

### Prerequisites
* Java JDK 17+
* Python 3.9+
* Docker & Docker Compose

### 1. 인프라 실행 (Docker Compose)
MariaDB, Redis, Elasticsearch 컨테이너를 실행합니다.
```bash
docker compose up -d mariadb redis elasticsearch
```

### 2. 백엔드 실행 (Spring Boot)
```bash
./gradlew bootRun
```
> 애플리케이션 접속: `http://localhost:8080`

### 3. AI 서버 실행 (FastAPI)
```bash
cd ai-server
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

---

<div align="center">
  <sub>WeStudy - Empowering collaborative learning with smart AI intelligence.</sub>
</div>
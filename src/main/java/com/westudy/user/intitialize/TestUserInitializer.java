package com.westudy.user.intitialize;

import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import com.westudy.study.event.StudyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("local")
@Slf4j
public class TestUserInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void run(String... args) throws Exception {
        // 1. 테스터 유저 15명 생성
        User tUser = userMapper.findByUsername("testuser0");
        List<Long> userIds = new ArrayList<>();
        
        if (tUser == null) {
            log.info("[MockData] 테스터 계정 15명 생성 중...");
            for (int i = 0; i < 15; i++) {
                User newUser = User.builder()
                        .username("testuser" + i)
                        .password(passwordEncoder.encode("testpassword"))
                        .email("testuser" + i + "@naver.com")
                        .nickname("테스터" + i)
                        .phoneNumber("010-0000-00" + (i < 10 ? "0" + i : i))
                        .role(UserRole.ROLE_USER)
                        .build();
                userMapper.insertUser(newUser);
                userIds.add(newUser.getId());
            }
            log.info("[MockData] 테스터 계정 자동 생성 완료");
        } else {
            // 이미 존재할 경우 ID 수집
            for (int i = 0; i < 15; i++) {
                User u = userMapper.findByUsername("testuser" + i);
                if (u != null) {
                    userIds.add(u.getId());
                }
            }
        }

        // 2. 멱등성 검사: 이미 스터디 mock 데이터가 삽입되었는지 체크
        Integer studyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM study WHERE title = 'Spring Boot 백엔드 대형 프로젝트 팀원 모집'", Integer.class);

        if (studyCount == null || studyCount == 0) {
            log.info("[MockData] 대규모 스터디/게시글 목 데이터 생성 시작...");
            
            // 20개 목 데이터 세트 정의
            String[][] mockStudies = {
                {
                    "Spring Boot 백엔드 대형 프로젝트 팀원 모집", "Project", "Spring Boot, Java, MySQL, Redis, AWS", "서울", "6", "RECRUITING",
                    "실제 배포 및 운영을 목표로 하는 백엔드 사이드 프로젝트입니다. Redis 캐싱과 인프라 CI/CD 배포까지 함께 학습하고 경험해 보실 백엔드 개발자 분들을 모십니다."
                },
                {
                    "React + TypeScript 프론트엔드 포트폴리오 스터디", "Project", "React, TypeScript, TailwindCSS, Next.js", "온라인", "4", "RECRUITING",
                    "디자이너와 협업하여 반응형 모바일/웹 서비스를 론칭할 프론트엔드 모임입니다. 코드 리뷰와 디자인 시스템 구성을 주축으로 진행합니다."
                },
                {
                    "코딩테스트 대비 파이썬 알고리즘 기초/심화반", "Algorithm", "Python, Java, C++", "대전", "5", "RECRUITING",
                    "백준 골드 등급 달성 및 프로그래머스 레벨 3 정복을 목표로 매주 4문제를 풀고 세미나를 진행하는 스터디입니다. 주 1회 오프라인 모임 진행합니다."
                },
                {
                    "정보처리기사 실기 동차 합격 벼락치기", "Cert", "SQL, C, Python", "온라인", "8", "RECRUITING",
                    "2026년 정처기 실기 기출 분석 및 예상 모의고사를 같이 풀이하고, 중요 프로그래밍 언어와 SQL 단답형 문제 위주로 암기 스터디를 진행합니다."
                },
                {
                    "토익 스피킹 2주 집중 AL 달성반", "Language", "English, Speaking, TOEIC", "부산", "4", "RECRUITING",
                    "매일 실전 모의고사를 1회 풀고 파트별 필수 템플릿 암기 및 상호 피드백을 진행하는 빡센 스터디입니다."
                },
                {
                    "Kubernetes & Docker DevOps 실무 가이드 스터디", "Other", "Docker, Kubernetes, Jenkins, Terraform", "인천", "5", "RECRUITING",
                    "인프라 구축 및 오케스트레이션 기초부터 실무 수준까지 학습합니다. 매주 도커 이미지 빌드 및 쿠버네티스 클러스터 배포 실습을 수행합니다."
                },
                {
                    "Kotlin & Android Jetpack Compose 클론 코딩 프로젝트", "Project", "Kotlin, Android Studio, Compose, Coroutine", "서울", "4", "CLOSED",
                    "최신 Jetpack Compose와 Coroutine을 활용해 당근마켓/배달의민족 중 하나를 선정하여 클론 코딩을 진행하는 스터디입니다. 현재 인원 모집이 완료되었습니다."
                },
                {
                    "Flutter를 이용한 하이브리드 앱 개발 모임", "Project", "Flutter, Dart, Firebase", "대구", "3", "IN_PROGRESS",
                    "1인 가구를 위한 위치 기반 커뮤니티 앱을 Flutter와 Dart를 활용해 빠르게 출시할 멤버를 모아 현재 개발을 착수했습니다."
                },
                {
                    "SQLD/SQLP 고득점 합격 스터디", "Cert", "Oracle, SQL, MySQL", "온라인", "6", "FINISHED",
                    "데이터 모델링 기본 및 SQL 활용 자격증 시험을 대비하여 요약 이론 회독 및 단원별 문제 풀이 학습을 진행했던 스터디입니다. 전원 합격 후 종료되었습니다."
                },
                {
                    "Node.js (NestJS) MSA 아키텍처 스터디", "Project", "NestJS, Node.js, RabbitMQ, Kafka, gRPC", "서울", "5", "RECRUITING",
                    "마이크로서비스 아키텍처(MSA) 패턴을 이해하고 NestJS 기반의 다중 서비스 통신 파이프라인을 설계하는 프로젝트성 모임입니다."
                },
                {
                    "Go 언어를 활용한 고성능 웹 서버 구축 스터디", "Project", "Go, Gin, PostgreSQL, Docker", "온라인", "4", "RECRUITING",
                    "Go (Golang)의 강력한 동시성 제어 기능을 학습하고 초당 수천 건의 요청을 응답하는 고성능 API 서버를 빌드해 봅니다."
                },
                {
                    "영어 회화 일상 프리토킹 & 패턴 암기 스터디", "Language", "English, Conversation", "서울", "6", "RECRUITING",
                    "매주 정해진 일상 주제(여행, 취미, 경제 등)에 대해 영어로 대화하며, 유용한 실전 회화 5가지 핵심 패턴을 암기하여 공유합니다."
                },
                {
                    "리액트 네이티브(React Native) 하이브리드 앱 출시", "Project", "React Native, Expo, Redux", "경기", "4", "RECRUITING",
                    "Expo 기반 개발 환경에서 가볍고 세련된 다이어리 앱을 런칭하는 스터디입니다. 현재 기획 완료 단계이며 퍼블리싱 담당하실 팀원을 모집합니다."
                },
                {
                    "AWS 솔루션 아키텍트(SAA) 자격증 스터디", "Cert", "AWS, Cloud, EC2, S3", "온라인", "6", "RECRUITING",
                    "AWS Certified Solutions Architect - Associate 자격 취득을 위해 덤프 문제 오답 노트 정리 및 클라우드 아키텍처 실습을 진행합니다."
                },
                {
                    "Vue.js 3 실무 프로젝트 스터디", "Project", "Vue.js, Pinia, Vite, Tailwind", "광주", "3", "RECRUITING",
                    "Vue 3 Composition API 문법을 바탕으로 어드민 대시보드 템플릿을 고도화하여 포트폴리오를 작성할 모임입니다."
                },
                {
                    "리눅스 마스터 1급/2급 기출 대비반", "Cert", "Linux, Shell Script", "온라인", "5", "RECRUITING",
                    "리눅스 시스템 명령어, 권한 관리, 기본 네트워킹 설정 이론 학습과 과년도 5개년 기출문제를 심층 분석하고 실기 실습을 같이 진행합니다."
                },
                {
                    "Next.js 14 App Router 기반 풀스택 프로젝트", "Project", "Next.js, React, Prisma, PostgreSQL", "서울", "5", "RECRUITING",
                    "Server Actions 및 다이나믹 라우팅을 이용해 세련된 e-커머스 플랫폼을 완성합니다. 프론트엔드와 백엔드 API를 동시에 경험하고 싶으신 분들 환영합니다."
                },
                {
                    "오타 보정 엘라스틱서치 검색 엔진 응용 스터디", "Other", "Elasticsearch, Logstash, Kibana", "온라인", "4", "RECRUITING",
                    "Nori 형태소 분석기와 Synonym(유의어) 사전을 정의하고 한글 오타 자동 수정(Fuzzy Match) 파이프라인을 실무 수준으로 구현하는 스터디입니다."
                },
                {
                    "토익 900점 돌파 매일 실전 R/C L/C 양치기", "Language", "TOEIC, English", "부산", "6", "RECRUITING",
                    "해커스 파랭이/빨갱이 실전 천제 문제집을 시간 재고 매일 풀이하며 단어 암기 체크 및 틀린 기출 패턴 오답 공유를 매일 오프라인으로 1시간씩 스파르타식으로 진행합니다."
                },
                {
                    "FastAPI를 활용한 경량 백엔드 API 개발", "Project", "FastAPI, Python, MongoDB, Docker", "울산", "4", "RECRUITING",
                    "파이썬 비동기 프레임워크인 FastAPI의 이점을 살려 실시간 채팅 또는 알림 API 서버를 빠르게 개발하고 문서 자동화 기능(Swagger)을 구성합니다."
                }
            };

            List<Long> generatedStudyIds = new ArrayList<>();

            for (int k = 0; k < mockStudies.length; k++) {
                String[] studyInfo = mockStudies[k];
                String title = studyInfo[0];
                String category = studyInfo[1];
                String techStacks = studyInfo[2];
                String location = studyInfo[3];
                int maxMember = Integer.parseInt(studyInfo[4]);
                String state = studyInfo[5];
                String content = studyInfo[6];

                long hostId = userIds.isEmpty() ? 1L : userIds.get(k % userIds.size());

                // 1) post 테이블 삽입
                KeyHolder postKeyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO post (user_id, views, category, title, summary, create_at) VALUES (?, 0, 'STUDY', ?, ?, now())",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setLong(1, hostId);
                    ps.setString(2, title);
                    ps.setString(3, title + " 요약");
                    return ps;
                }, postKeyHolder);
                long postId = postKeyHolder.getKey().longValue();

                // 2) post_content 테이블 삽입
                jdbcTemplate.update(
                        "INSERT INTO post_content (post_id, content) VALUES (?, ?)",
                        postId, content
                );

                // 3) study 테이블 삽입
                final int dayOffset = k;
                KeyHolder studyKeyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO study (post_id, user_id, title, location, max_member, deadline, state, tech_stacks, category, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now())",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setLong(1, postId);
                    ps.setLong(2, hostId);
                    ps.setString(3, title);
                    ps.setString(4, location);
                    ps.setInt(5, maxMember);
                    ps.setObject(6, LocalDateTime.now().plusDays(14 + dayOffset));
                    ps.setString(7, state);
                    ps.setString(8, techStacks);
                    ps.setString(9, category);
                    return ps;
                }, studyKeyHolder);
                long studyId = studyKeyHolder.getKey().longValue();
                generatedStudyIds.add(studyId);

                // 4) study_participant 방장(호스트) 자동 승인 삽입
                jdbcTemplate.update(
                        "INSERT INTO study_participant (study_id, user_id, status) VALUES (?, ?, 'APPROVED')",
                        studyId, hostId
                );

                // 5) 추가 참여자 자동 매핑
                int extraMembersCount = (k % 3) + 1; // 1 ~ 3명
                for (int m = 1; m <= extraMembersCount; m++) {
                    if (m < maxMember) {
                        long partUserId = userIds.get((k + m) % userIds.size());
                        if (partUserId != hostId) {
                            jdbcTemplate.update(
                                    "INSERT INTO study_participant (study_id, user_id, status) VALUES (?, ?, 'APPROVED')",
                                    studyId, partUserId
                            );
                        }
                    }
                }

                // 6) 스터디 일지(로그) 추가 삽입
                if (k % 2 == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO study_log (study_id, user_id, title, content, created_at, modified_at) VALUES (?, ?, ?, ?, now(), now())",
                            studyId, hostId, "1주차 스터디 오리엔테이션 및 학습 기획",
                            "오늘 1주차 첫 비대면 미팅을 가졌습니다.\n앞으로 격주로 진척도를 체크하고 해당 위키(일지) 보관소에 정리하기로 결정하였습니다.\n\n[참고 사항]\n- 매주 화요일/목요일 저녁 8시 고정 모임\n- 1차 목표: 코어 구조 및 설계 명세서 리뷰"
                    );
                    jdbcTemplate.update(
                            "INSERT INTO study_log (study_id, user_id, title, content, created_at, modified_at) VALUES (?, ?, ?, ?, now(), now())",
                            studyId, hostId, "2주차 개념 이론 학습 및 핵심 실습 회고",
                            "오늘 2주차 모임을 완료했습니다. 개인별 이론 준비 부분을 발표하고 공유했습니다.\n궁금한 질문은 메인 게시판의 댓글이나 카카오 채널을 통해 자유롭게 질의응답 해주세요."
                    );
                }
            }

            log.info("[MockData] 대규모 스터디 DB 목 데이터 삽입 성공 - {} 건", generatedStudyIds.size());

            // 3. 생성된 스터디 건들에 대해 Elasticsearch SAVE 이벤트 강제 발행
            log.info("[MockData] 생성된 스터디 대상 Elasticsearch 색인(인덱싱) 동기화 이벤트 순차 발행 중...");
            for (Long studyId : generatedStudyIds) {
                eventPublisher.publishEvent(new StudyEvent(studyId, "SAVE"));
            }
            log.info("[MockData] Elasticsearch 색인 동기화 완료.");
        }
    }
}

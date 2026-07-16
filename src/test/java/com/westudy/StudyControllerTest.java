package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.enums.StudyStates;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import com.westudy.user.dto.UserLoginDTO;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional // 각 테스트 완료 후 DB 롤백 처리 활성화
public class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudyMapper studyMapper;

    @Autowired
    private StudyParticipantMapper studyParticipantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger log = LoggerFactory.getLogger(StudyControllerTest.class);

    private Cookie[] authCookies;
    private Long hostUserId;
    private Long waiterUserId;
    private Long waiter2UserId;

    @BeforeEach
    void setupDatabaseAndLogin() throws Exception {
        // 0. 기존 컨테이너 DB 컬럼 정합성을 위해 deadline 관련 컬럼 추가
        try {
            jdbcTemplate.execute("ALTER TABLE study ADD COLUMN IF NOT EXISTS deadline DATETIME NULL");
        } catch (Exception e) {
            log.warn("스터디 테이블 컬럼 추가 실패 혹은 이미 존재함: {}", e.getMessage());
        }

        // 1. 테스트 호스트 회원 생성 (없을 경우에만 추가)
        String hostEmail = "testuser@naver.com";
        User hostUser = userMapper.findByEmail(hostEmail);
        if (hostUser == null) {
            User newUser = User.builder()
                    .username("testuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(hostEmail)
                    .nickname("호스트닉네임")
                    .phoneNumber("010-0000-0000")
                    .role(UserRole.ROLE_USER)
                    .build();
            userMapper.insertUser(newUser);
            hostUserId = newUser.getId();
        } else {
            hostUserId = hostUser.getId();
        }

        // 2. 테스트 대기자 1 생성 (없을 경우에만 추가)
        String waiterEmail = "waiter1@naver.com";
        User waiterUser = userMapper.findByEmail(waiterEmail);
        if (waiterUser == null) {
            User newUser = User.builder()
                    .username("waiter1")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS")
                    .email(waiterEmail)
                    .nickname("대기자1닉네임")
                    .phoneNumber("010-1111-1111")
                    .role(UserRole.ROLE_USER)
                    .build();
            userMapper.insertUser(newUser);
            waiterUserId = newUser.getId();
        } else {
            waiterUserId = waiterUser.getId();
        }

        // 3. 테스트 대기자 2 생성 (없을 경우에만 추가)
        String waiter2Email = "waiter2@naver.com";
        User waiter2User = userMapper.findByEmail(waiter2Email);
        if (waiter2User == null) {
            User newUser = User.builder()
                    .username("waiter2")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS")
                    .email(waiter2Email)
                    .nickname("대기자2닉네임")
                    .phoneNumber("010-2222-2222")
                    .role(UserRole.ROLE_USER)
                    .build();
            userMapper.insertUser(newUser);
            waiter2UserId = newUser.getId();
        } else {
            waiter2UserId = waiter2User.getId();
        }

        // 4. 호스트 계정으로 로그인 후 인증 쿠키 획득
        UserLoginDTO loginDto = new UserLoginDTO();
        loginDto.setEmail(hostEmail);
        loginDto.setPassword("testpassword");

        authCookies = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
    }

    @Test
    @DisplayName("스터디 라이프사이클 통합 테스트 - 등록, 수정, 가입신청, 취소, 승인, 거부, 삭제의 전체 DB 동작 검증")
    void testStudyLifecycle() throws Exception {
        // [1] 스터디 등록
        StudyInsertDTO insertDto = new StudyInsertDTO();
        insertDto.setTitle("통합 테스트 스터디");
        insertDto.setLocation("서울");
        insertDto.setMaxMember(5);

        mockMvc.perform(post("/api/study/insert")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDto)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 스터디 등록 확인 및 생성된 ID 추출
        List<StudyResponseDTO> hostStudies = studyMapper.findStudy(10, 0);
        StudyResponseDTO createdStudy = hostStudies.stream()
                .filter(s -> s.getTitle().equals("통합 테스트 스터디"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("스터디가 DB에 등록되지 않았습니다."));
        long dynamicStudyId = createdStudy.getId();

        // [2] 스터디 수정
        StudyUpdateDTO updateDto = new StudyUpdateDTO();
        updateDto.setId(dynamicStudyId);
        updateDto.setTitle("수정된 통합 테스트 스터디");
        updateDto.setLocation("인천");
        updateDto.setMaxMember(8);

        mockMvc.perform(post("/api/study/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 수정 여부 확인
        StudyResponseDTO updatedStudy = studyMapper.findByStudyId(dynamicStudyId);
        assertEquals("수정된 통합 테스트 스터디", updatedStudy.getTitle());
        assertEquals("인천", updatedStudy.getLocation());

        // [3] 타인이 개설한 스터디에 호스트가 가입 신청
        // 타인(대기자1)의 스터디 생성
        Study waiterStudy = Study.builder()
                .userId(waiterUserId)
                .title("대기자가 만든 스터디")
                .location("부산")
                .maxMember(4)
                .state(StudyStates.RECRUITING)
                .deadline(LocalDateTime.now().plusDays(5))
                .build();
        studyMapper.insertStudy(waiterStudy);
        long waiterStudyId = waiterStudy.getId();

        // 가입 신청 API 호출
        mockMvc.perform(post("/api/study/application")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waiterStudyId)))
                .andExpect(status().isOk());

        // DB 검증: 신청 여부 확인
        int checkApplication = studyParticipantMapper.findByUserIdAndStudyId(hostUserId, waiterStudyId);
        assertEquals(1, checkApplication, "호스트가 대기자 스터디에 신청 완료된 상태여야 합니다.");

        // [4] 가입 신청 취소
        mockMvc.perform(post("/api/study/cancel")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waiterStudyId)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 신청 취소 완료 확인 (소프트 캔슬 여부 확인)
        StudyParticipant canceledParticipant = studyParticipantMapper.findByUserId(hostUserId);
        assertNotNull(canceledParticipant);
        assertEquals(StudyParticipantStatus.CANCELLED, canceledParticipant.getStatus(), "신청 취소 후 상태가 CANCELLED여야 합니다.");

        // [5] 내 스터디에 대기자1이 신청한 후 승인 처리 검증
        // 대기자1의 신청 데이터를 DB에 직접 적재
        StudyParticipant participant1 = StudyParticipant.builder()
                .studyId(dynamicStudyId)
                .userId(waiterUserId)
                .status(StudyParticipantStatus.WAITING)
                .build();
        studyParticipantMapper.insertStudyParticipant(participant1);

        // 승인 요청 API 호출
        Map<String, Object> approveBody = new HashMap<>();
        approveBody.put("studyId", dynamicStudyId);
        approveBody.put("userId", waiterUserId);

        mockMvc.perform(post("/api/study/approve")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveBody)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 대기자1 승인 상태 확인
        StudyParticipant approvedParticipant = studyParticipantMapper.findByUserId(waiterUserId);
        assertNotNull(approvedParticipant);
        assertEquals(StudyParticipantStatus.APPROVED, approvedParticipant.getStatus());

        // [6] 내 스터디에 대기자2가 신청한 후 거절 처리 검증
        // 대기자2의 신청 데이터를 DB에 직접 적재
        StudyParticipant participant2 = StudyParticipant.builder()
                .studyId(dynamicStudyId)
                .userId(waiter2UserId)
                .status(StudyParticipantStatus.WAITING)
                .build();
        studyParticipantMapper.insertStudyParticipant(participant2);

        // 거부 요청 API 호출
        Map<String, Object> rejectBody = new HashMap<>();
        rejectBody.put("studyId", dynamicStudyId);
        rejectBody.put("userId", waiter2UserId);

        mockMvc.perform(post("/api/study/reject")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectBody)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 대기자2 거부 상태 확인
        StudyParticipant rejectedParticipant = studyParticipantMapper.findByUserId(waiter2UserId);
        assertNotNull(rejectedParticipant);
        assertEquals(StudyParticipantStatus.REJECTED, rejectedParticipant.getStatus());

        // [7] 스터디 삭제
        mockMvc.perform(post("/api/study/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(dynamicStudyId)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("스터디 상세 조회 시 모델 바인딩 및 가입 신청 상태 전이 검증")
    void testStudyDetailModelBindingAndStatusTransitions() throws Exception {
        // [1] 스터디 및 연결 게시글 DB 직접 등록
        // 1-1. 게시글 생성
        jdbcTemplate.update(
            "INSERT INTO post (user_id, views, category, title, summary) VALUES (?, 0, 'STUDY', '상세 모델 바인딩 테스트 글', 'Summary')",
            hostUserId
        );
        long testPostId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbcTemplate.update("INSERT INTO post_content (post_id, content) VALUES (?, '본문 내용')", testPostId);

        // 1-2. 스터디 생성
        jdbcTemplate.update(
            "INSERT INTO study (post_id, user_id, title, location, max_member, state, created_at) VALUES (?, ?, '상세 모델 바인딩 테스트 글', '서울', 3, 'RECRUITING', NOW())",
            testPostId, hostUserId
        );
        long testStudyId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbcTemplate.update("UPDATE post SET study_id = ? WHERE id = ?", testStudyId, testPostId);

        // [2] 방장 계정으로 상세 페이지 조회 검증
        org.springframework.test.web.servlet.MvcResult hostResult = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("isHost"))
                .andExpect(model().attributeExists("currentMemberCount"))
                .andReturn();

        Boolean isHost = (Boolean) hostResult.getModelAndView().getModel().get("isHost");
        assertTrue(isHost, "방장이 본인의 스터디 상세 페이지를 조회하면 isHost가 true여야 합니다.");
        
        Integer currentMemberCount = (Integer) hostResult.getModelAndView().getModel().get("currentMemberCount");
        assertEquals(0, currentMemberCount, "초기 승인된 참가자 수는 0명이어야 합니다.");

        // [3] 대기자 1 계정으로 로그인 후 상세 페이지 조회 검증
        UserLoginDTO waiterLogin = new UserLoginDTO();
        waiterLogin.setEmail("waiter1@naver.com");
        waiterLogin.setPassword("testpassword");

        Cookie[] waiterCookies = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(waiterLogin)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();

        org.springframework.test.web.servlet.MvcResult waiter1ResultBefore = mockMvc.perform(get("/page/post/detail")
                        .cookie(waiterCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isHost"))
                .andReturn();

        Boolean isHostForWaiter = (Boolean) waiter1ResultBefore.getModelAndView().getModel().get("isHost");
        assertFalse(isHostForWaiter, "일반 회원이 조회할 경우 isHost는 false여야 합니다.");
        assertNull(waiter1ResultBefore.getModelAndView().getModel().get("participantStatus"), "신청 전에는 상태가 null이어야 합니다.");

        // [4] 대기자 1의 참가 신청 API 실행
        mockMvc.perform(post("/api/study/application")
                        .cookie(waiterCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudyId)))
                .andExpect(status().isOk());

        // 대기자 1 시점에서 상세 페이지 재조회 시 상태 WAITING 검증
        org.springframework.test.web.servlet.MvcResult waiter1ResultAfter = mockMvc.perform(get("/page/post/detail")
                        .cookie(waiterCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("WAITING", waiter1ResultAfter.getModelAndView().getModel().get("participantStatus"));

        // [5] 방장 시점에서 상세 페이지 재조회 시 신청자 목록 및 대기 상태 검증
        org.springframework.test.web.servlet.MvcResult hostResultAfterApp = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("applicants"))
                .andReturn();

        List<?> applicants = (List<?>) hostResultAfterApp.getModelAndView().getModel().get("applicants");
        assertNotNull(applicants);
        assertEquals(1, applicants.size(), "신청자가 1명 등록되어 있어야 합니다.");
        
        com.westudy.study.dto.StudyParticipanResponseDTO applicantDto = (com.westudy.study.dto.StudyParticipanResponseDTO) applicants.get(0);
        assertEquals(waiterUserId, applicantDto.getUserId());
        assertEquals("WAITING", applicantDto.getStatus().name());
    }

    @Test
    @DisplayName("스터디 목록 조회 및 검색 페이지 SSR 연동 검증")
    void testStudyListPageAndSearchPage() throws Exception {
        // [0] 스터디 및 연결 게시글 DB 직접 등록
        jdbcTemplate.update(
            "INSERT INTO post (user_id, views, category, title, summary) VALUES (?, 0, 'STUDY', '목록용 테스트 스터디', 'Summary')",
            hostUserId
        );
        long testPostId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbcTemplate.update("INSERT INTO post_content (post_id, content) VALUES (?, '본문 내용')", testPostId);

        jdbcTemplate.update(
            "INSERT INTO study (post_id, user_id, title, location, max_member, state, created_at) VALUES (?, ?, '목록용 테스트 스터디', '서울', 3, 'RECRUITING', NOW())",
            testPostId, hostUserId
        );
        long testStudyId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        jdbcTemplate.update("UPDATE post SET study_id = ? WHERE id = ?", testStudyId, testPostId);

        // 참가 신청자 1명 APPROVED 상태 등록
        jdbcTemplate.update(
            "INSERT INTO study_participant (study_id, user_id, status, joined_at) VALUES (?, ?, 'APPROVED', NOW())",
            testStudyId, waiterUserId
        );

        // [1] 스터디 목록 조회 검증
        org.springframework.test.web.servlet.MvcResult listResult = mockMvc.perform(get("/page/study")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/layout/study/board"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("pageCount"))
                .andExpect(model().attributeExists("currentPage"))
                .andReturn();

        List<?> pages = (List<?>) listResult.getModelAndView().getModel().get("pages");
        assertNotNull(pages);
        assertFalse(pages.isEmpty());
        
        com.westudy.study.dto.StudyResponseDTO firstStudy = (com.westudy.study.dto.StudyResponseDTO) pages.stream()
                .filter(p -> ((com.westudy.study.dto.StudyResponseDTO) p).getId() == testStudyId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("등록한 테스트 스터디를 목록에서 찾을 수 없습니다."));
        assertEquals(1, firstStudy.getApprovedMemberCount(), "서브쿼리 매핑에 따른 승인 멤버 수(approvedMemberCount)는 1이어야 합니다.");

        // [2] 스터디 키워드 검색 조회 검증
        mockMvc.perform(get("/page/study/search")
                        .param("keyword", "목록용")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/layout/study/board"))
                .andExpect(model().attributeExists("pages"))
                .andExpect(model().attributeExists("pageCount"))
                .andExpect(model().attributeExists("currentPage"));
    }
}

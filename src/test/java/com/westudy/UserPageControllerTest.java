package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.study.dto.MyPageStudyDTO;
import com.westudy.user.dto.UserLoginDTO;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Cookie[] authCookies;
    private long testUserId;

    @BeforeEach
    void setupUserAndLogin() throws Exception {
        String email = "mypagetest@naver.com";
        User existing = userMapper.findByEmail(email);
        if (existing == null) {
            User testUser = User.builder()
                    .username("mypageuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(email)
                    .nickname("마이페이지테스터")
                    .phoneNumber("010-9999-9999")
                    .role(UserRole.ROLE_USER)
                    .build();
            userMapper.insertUser(testUser);
            testUserId = testUser.getId();
        } else {
            testUserId = existing.getId();
        }

        UserLoginDTO loginDto = new UserLoginDTO();
        loginDto.setEmail(email);
        loginDto.setPassword("testpassword");

        authCookies = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
    }

    private long insertPost(long userId, String title) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                "INSERT INTO post (user_id, views, category, title, summary) VALUES (?, 0, 'FREE', ?, 'Summary')",
                java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, userId);
            ps.setString(2, title);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private long insertStudy(long postId, long userId, String title, String location, int maxMember, String state) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                "INSERT INTO study (post_id, user_id, title, location, max_member, state) VALUES (?, ?, ?, ?, ?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, postId);
            ps.setLong(2, userId);
            ps.setString(3, title);
            ps.setString(4, location);
            ps.setInt(5, maxMember);
            ps.setString(6, state);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Test
    @DisplayName("마이페이지 대시보드 진입 시 스터디 실제 데이터 값 매핑 검증")
    @SuppressWarnings("unchecked")
    void testUserPageDashboardBindingsAndValues() throws Exception {
        // [1] 테스트용 게시글 및 스터디 데이터 삽입
        long p1 = insertPost(testUserId, "Post 1");
        long s1 = insertStudy(p1, testUserId, "참여 스터디 테스트", "서울", 4, "RECRUITING");
        // 참여자 추가 (APPROVED)
        jdbcTemplate.update("INSERT INTO study_participant (study_id, user_id, status) VALUES (?, ?, 'APPROVED')", s1, testUserId);

        long p2 = insertPost(testUserId, "Post 2");
        long s2 = insertStudy(p2, testUserId, "개설 스터디 테스트", "부산", 6, "IN_PROGRESS");
        // 개설한 스터디의 방장은 개설 조건만으로 opened에 집계됨

        long p3 = insertPost(testUserId, "Post 3");
        long s3 = insertStudy(p3, testUserId, "북마크 스터디 테스트", "대전", 8, "RECRUITING");
        // 북마크 등록
        jdbcTemplate.update("INSERT INTO bookmark (post_id, user_id) VALUES (?, ?)", p3, testUserId);

        // 알림 등록
        jdbcTemplate.update("INSERT INTO alarm (receiver_id, sender_id, type, content, target_url, is_read) VALUES (?, ?, 'STUDY_APPROVE', '테스트 알림 내용', '/target', false)", testUserId, testUserId);

        // [2] 마이페이지 대시보드 요청 실행
        MvcResult mvcResult = mockMvc.perform(get("/page/user/info/data")
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(view().name("/layout/user/userpage"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("participatingStudies"))
                .andExpect(model().attributeExists("openedStudies"))
                .andExpect(model().attributeExists("bookmarkedStudies"))
                .andExpect(model().attributeExists("alarms"))
                .andReturn();

        // [3] 모델 값 캐스팅 및 검증
        var modelMap = mvcResult.getModelAndView().getModel();
        List<MyPageStudyDTO> participating = (List<MyPageStudyDTO>) modelMap.get("participatingStudies");
        List<MyPageStudyDTO> opened = (List<MyPageStudyDTO>) modelMap.get("openedStudies");
        List<MyPageStudyDTO> bookmarked = (List<MyPageStudyDTO>) modelMap.get("bookmarkedStudies");
        List<com.westudy.alarm.dto.AlarmResponseDTO> alarms = (List<com.westudy.alarm.dto.AlarmResponseDTO>) modelMap.get("alarms");

        assertNotNull(participating);
        assertNotNull(opened);
        assertNotNull(bookmarked);

        // [4] 실제 저장된 값과 DB에서 연동된 데이터 정합성 검증
        // 4-1. 참여중인 스터디 검증
        assertFalse(participating.isEmpty(), "참여중인 스터디 목록에 데이터가 들어있어야 합니다.");
        MyPageStudyDTO partStudy = participating.stream()
                .filter(s -> s.getId() == s1)
                .findFirst()
                .orElseThrow(() -> new AssertionError("참여중인 스터디 목록에 추가한 스터디가 존재하지 않습니다."));
        assertEquals("참여 스터디 테스트", partStudy.getTitle());
        assertEquals("서울", partStudy.getLocation());
        assertEquals(4, partStudy.getMaxMember());
        assertEquals(1, partStudy.getCurrentMemberCount(), "승인된 참가자 수는 1명이어야 합니다.");

        // 4-2. 개설한 스터디 검증
        assertFalse(opened.isEmpty(), "개설한 스터디 목록에 데이터가 들어있어야 합니다.");
        MyPageStudyDTO openStudy = opened.stream()
                .filter(s -> s.getId() == s2)
                .findFirst()
                .orElseThrow(() -> new AssertionError("개설한 스터디 목록에 추가한 스터디가 존재하지 않습니다."));
        assertEquals("개설 스터디 테스트", openStudy.getTitle());
        assertEquals("부산", openStudy.getLocation());
        assertEquals(6, openStudy.getMaxMember());

        // 4-3. 관심(북마크) 스터디 검증
        assertFalse(bookmarked.isEmpty(), "관심 스터디 목록에 데이터가 들어있어야 합니다.");
        MyPageStudyDTO bookStudy = bookmarked.stream()
                .filter(s -> s.getId() == s3)
                .findFirst()
                .orElseThrow(() -> new AssertionError("관심 스터디 목록에 추가한 스터디가 존재하지 않습니다."));
        assertEquals("북마크 스터디 테스트", bookStudy.getTitle());
        assertEquals("대전", bookStudy.getLocation());
        assertEquals(8, bookStudy.getMaxMember());

        // 4-4. 알림 내역 검증
        assertNotNull(alarms);
        assertFalse(alarms.isEmpty(), "알림 목록에 데이터가 들어있어야 합니다.");
        com.westudy.alarm.dto.AlarmResponseDTO alarm = alarms.get(0);
        assertEquals("테스트 알림 내용", alarm.getContent());
        assertEquals("/target", alarm.getTargetUrl());
        assertFalse(alarm.isRead());
        assertEquals(com.westudy.alarm.enums.AlarmType.STUDY_APPROVE, alarm.getType());
    }
}

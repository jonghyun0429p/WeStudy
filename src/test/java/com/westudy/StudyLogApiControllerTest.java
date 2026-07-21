package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.studylog.dto.StudyLogRequestDTO;
import com.westudy.studylog.dto.StudyLogResponseDTO;
import com.westudy.studylog.entity.StudyLog;
import com.westudy.studylog.mapper.StudyLogMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudyLogApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudyLogMapper studyLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Cookie[] hostCookies;
    private Cookie[] memberCookies;
    private Cookie[] outsiderCookies;

    private long hostId;
    private long memberId;
    private long outsiderId;

    private long testStudyId;

    @BeforeEach
    void setupDataAndLogin() throws Exception {
        // 1. 유저들 생성 및 로그인
        hostId = createUser("loghost", "loghost@naver.com", "방장닉");
        memberId = createUser("logmember", "logmember@naver.com", "멤버닉");
        outsiderId = createUser("logoutsider", "logoutsider@naver.com", "외부인닉");

        hostCookies = performLogin("loghost@naver.com");
        memberCookies = performLogin("logmember@naver.com");
        outsiderCookies = performLogin("logoutsider@naver.com");

        // 2. 스터디 데이터 생성
        long postId = insertPost(hostId, "스터디 모집글");
        testStudyId = insertStudy(postId, hostId, "스터디명", "서울", 4, "RECRUITING");

        // 3. 멤버십 설정
        // member는 APPROVED
        jdbcTemplate.update("INSERT INTO study_participant (study_id, user_id, status) VALUES (?, ?, 'APPROVED')", testStudyId, memberId);
        // outsider는 참여 안 함
    }

    private long createUser(String username, String email, String nickname) {
        User existing = userMapper.findByEmail(email);
        if (existing != null) {
            return existing.getId();
        }
        User user = User.builder()
                .username(username)
                .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                .email(email)
                .nickname(nickname)
                .phoneNumber("010-0000-0000")
                .role(UserRole.ROLE_USER)
                .build();
        userMapper.insertUser(user);
        return user.getId();
    }

    private Cookie[] performLogin(String email) throws Exception {
        UserLoginDTO loginDto = new UserLoginDTO();
        loginDto.setEmail(email);
        loginDto.setPassword("testpassword");

        return mockMvc.perform(post("/api/users/login")
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
    @DisplayName("스터디 일지(로그) CRUD 권한별 동작 검증 통합 테스트")
    void testStudyLogCrudPermissions() throws Exception {
        // [1] 일지 생성 검증
        // 1-1. 스터디 멤버(APPROVED)가 일지 생성 성공
        StudyLogRequestDTO log1 = StudyLogRequestDTO.builder()
                .title("1회차 일지")
                .content("학습 요약 1")
                .build();

        mockMvc.perform(post("/api/studies/" + testStudyId + "/logs")
                        .cookie(memberCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log1)))
                .andExpect(status().isOk());

        // 1-2. 방장이 일지 생성 성공
        StudyLogRequestDTO log2 = StudyLogRequestDTO.builder()
                .title("2회차 일지")
                .content("학습 요약 2")
                .build();

        mockMvc.perform(post("/api/studies/" + testStudyId + "/logs")
                        .cookie(hostCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log2)))
                .andExpect(status().isOk());

        // 1-3. 외부인이 일지 생성 시도 시 401/권한 거부
        mockMvc.perform(post("/api/studies/" + testStudyId + "/logs")
                        .cookie(outsiderCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(log1)))
                .andExpect(status().isUnauthorized()); // SC_UNAUTHORIZED (401)

        // [2] 일지 조회 검증
        // 2-1. 스터디 멤버가 일지 목록 조회 시 본인 일지는 editable=true, 남의 것은 editable=false
        mockMvc.perform(get("/api/studies/" + testStudyId + "/logs")
                        .cookie(memberCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.title == '1회차 일지')].editable").value(true))
                .andExpect(jsonPath("$[?(@.title == '2회차 일지')].editable").value(false));

        // 2-2. 방장이 조회 시에는 모든 일지가 editable=true
        mockMvc.perform(get("/api/studies/" + testStudyId + "/logs")
                        .cookie(hostCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].editable").value(true))
                .andExpect(jsonPath("$[1].editable").value(true));

        // 2-3. 외부인이 조회 시도 시 권한 거부
        mockMvc.perform(get("/api/studies/" + testStudyId + "/logs")
                        .cookie(outsiderCookies))
                .andExpect(status().isUnauthorized());

        // [3] 일지 수정 권한 검증
        List<StudyLogResponseDTO> logsInDb = studyLogMapper.findByStudyId(testStudyId);
        StudyLogResponseDTO memberLog = logsInDb.stream().filter(l -> l.getTitle().equals("1회차 일지")).findFirst().get();
        StudyLogResponseDTO hostLog = logsInDb.stream().filter(l -> l.getTitle().equals("2회차 일지")).findFirst().get();

        // 3-1. 멤버가 본인 일지 수정 성공
        StudyLogRequestDTO updateRequest = StudyLogRequestDTO.builder()
                .title("1회차 일지 수정")
                .content("학습 요약 1 수정완료")
                .build();

        mockMvc.perform(put("/api/studies/" + testStudyId + "/logs/" + memberLog.getId())
                        .cookie(memberCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // DB 확인
        StudyLog updatedFromDb = studyLogMapper.findById(memberLog.getId());
        assertEquals("1회차 일지 수정", updatedFromDb.getTitle());
        assertEquals("학습 요약 1 수정완료", updatedFromDb.getContent());

        // 3-2. 멤버가 방장 일지 수정 시도 시 권한 거부
        mockMvc.perform(put("/api/studies/" + testStudyId + "/logs/" + hostLog.getId())
                        .cookie(memberCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());

        // 3-3. 방장이 멤버 일지 수정 성공
        StudyLogRequestDTO hostModifyRequest = StudyLogRequestDTO.builder()
                .title("방장이 수정한 제목")
                .content("내용")
                .build();

        mockMvc.perform(put("/api/studies/" + testStudyId + "/logs/" + memberLog.getId())
                        .cookie(hostCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hostModifyRequest)))
                .andExpect(status().isOk());

        // [4] 일지 삭제 권한 검증
        // 4-1. 외부인이 삭제 시도 시 거부
        mockMvc.perform(delete("/api/studies/" + testStudyId + "/logs/" + memberLog.getId())
                        .cookie(outsiderCookies))
                .andExpect(status().isUnauthorized());

        // 4-2. 방장이 멤버 일지 삭제 성공
        mockMvc.perform(delete("/api/studies/" + testStudyId + "/logs/" + memberLog.getId())
                        .cookie(hostCookies))
                .andExpect(status().isOk());

        assertNull(studyLogMapper.findById(memberLog.getId()), "삭제된 일지는 조회되지 않아야 합니다.");
    }
}

package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.chat.dto.ChatRoomResponseDTO;
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
public class ChatControllerTest {

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
        String email = "chattest@naver.com";
        User existing = userMapper.findByEmail(email);
        if (existing == null) {
            User testUser = User.builder()
                    .username("chatuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(email)
                    .nickname("채팅테스터")
                    .phoneNumber("010-8888-8888")
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
    @DisplayName("나의 채팅방 로비 조회 및 REST API 데이터 정합성 검증")
    @SuppressWarnings("unchecked")
    void testChatRoomsLobbyAndData() throws Exception {
        // [1] 테스트 데이터 세팅
        // 1-1. 내가 방장인 스터디 개설
        long p1 = insertPost(testUserId, "Post 1");
        long s1 = insertStudy(p1, testUserId, "내가 개설한 스터디", "온라인", 5, "RECRUITING");

        // 1-2. 다른 사람이 개설했고 내가 참가자(APPROVED)인 스터디
        User otherUser = User.builder()
                .username("otheruser")
                .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS")
                .email("other@naver.com")
                .nickname("타인")
                .phoneNumber("010-7777-7777")
                .role(UserRole.ROLE_USER)
                .build();
        userMapper.insertUser(otherUser);

        long p2 = insertPost(otherUser.getId(), "Post 2");
        long s2 = insertStudy(p2, otherUser.getId(), "내가 참여한 스터디", "오프라인", 10, "IN_PROGRESS");
        jdbcTemplate.update("INSERT INTO study_participant (study_id, user_id, status) VALUES (?, ?, 'APPROVED')", s2, testUserId);

        // 1-3. 최근 메시지 삽입
        jdbcTemplate.update("INSERT INTO chat_message (study_id, sender_id, message) VALUES (?, ?, '최근 전송 메시지')", s1, testUserId);

        // [2] MVC /page/chat 페이지 모델 바인딩 검증
        MvcResult mvcResult = mockMvc.perform(get("/page/chat")
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(view().name("layout/chat/lobby"))
                .andExpect(model().attributeExists("rooms"))
                .andReturn();

        var modelMap = mvcResult.getModelAndView().getModel();
        List<ChatRoomResponseDTO> mvcRooms = (List<ChatRoomResponseDTO>) modelMap.get("rooms");
        assertNotNull(mvcRooms);
        assertEquals(2, mvcRooms.size(), "내가 가입된 대화방은 총 2개여야 합니다.");

        // [3] REST API /api/chat/rooms JSON 데이터 값 대조 정합성 검증
        mockMvc.perform(get("/api/chat/rooms")
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                // 첫번째 방 (최근 대화가 있으므로 최상단 정렬)
                .andExpect(jsonPath("$[0].studyId").value(s1))
                .andExpect(jsonPath("$[0].title").value("내가 개설한 스터디"))
                .andExpect(jsonPath("$[0].location").value("온라인"))
                .andExpect(jsonPath("$[0].maxMember").value(5))
                .andExpect(jsonPath("$[0].host").value(true))
                .andExpect(jsonPath("$[0].lastMessage").value("최근 전송 메시지"))
                // 두번째 방
                .andExpect(jsonPath("$[1].studyId").value(s2))
                .andExpect(jsonPath("$[1].title").value("내가 참여한 스터디"))
                .andExpect(jsonPath("$[1].location").value("오프라인"))
                .andExpect(jsonPath("$[1].maxMember").value(10))
                .andExpect(jsonPath("$[1].host").value(false))
                .andExpect(jsonPath("$[1].lastMessage").isEmpty());
    }
}

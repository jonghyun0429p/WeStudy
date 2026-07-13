package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.bookmark.dto.BookmarkToggleRequestDTO;
import com.westudy.bookmark.mapper.BookmarkMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookmarkMapper bookmarkMapper;

    private Cookie[] authCookies;
    private long testUserId;
    private long testPostId;

    @BeforeEach
    void setupUserAndData() throws Exception {
        // 1. 기존 유저 생성 및 로그인
        String email = "bookmarktest@naver.com";
        User existing = userMapper.findByEmail(email);
        if (existing == null) {
            User testUser = User.builder()
                    .username("bookmarkuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(email)
                    .nickname("북마크테스터")
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

        // 2. 테스트용 게시글 생성 (조인 테이블인 post_content 포함)
        testPostId = insertPost(testUserId, "테스트 북마크 타겟 글");
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
        long postId = keyHolder.getKey().longValue();

        // post_content 테이블에 필수 조인 레코드 추가
        jdbcTemplate.update("INSERT INTO post_content (post_id, content) VALUES (?, '테스트 본문 내용')", postId);

        return postId;
    }

    @Test
    @DisplayName("북마크 토글 및 상세페이지 렌더링 연동 통합 테스트")
    void testBookmarkToggleAndDetailBinding() throws Exception {
        // [1] 초기 상태: 북마크 미등록 확인
        int initialCount = bookmarkMapper.isBookmarked(testPostId, testUserId);
        assertEquals(0, initialCount, "처음에는 북마크가 존재하지 않아야 합니다.");

        // [2] 북마크 등록 (Toggle ON)
        BookmarkToggleRequestDTO request = new BookmarkToggleRequestDTO();
        request.setPostId(testPostId);

        mockMvc.perform(post("/api/bookmarks/toggle")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(true));

        // DB 정합성 검증: 실제 bookmark 테이블에 레코드가 1개 생겼는지 확인
        int countAfterAdd = bookmarkMapper.isBookmarked(testPostId, testUserId);
        assertEquals(1, countAfterAdd, "북마크 등록 후 DB에 레코드가 적재되어 있어야 합니다.");

        // [3] 상세페이지 조회 시 isBookmarked 가 true로 바인딩되는지 검증
        MvcResult detailResultAfterAdd = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isBookmarked"))
                .andReturn();

        Boolean isBookmarkedAfterAdd = (Boolean) detailResultAfterAdd.getModelAndView().getModel().get("isBookmarked");
        assertNotNull(isBookmarkedAfterAdd);
        assertTrue(isBookmarkedAfterAdd, "북마크 등록 상태에서 상세페이지 진입 시 isBookmarked는 true여야 합니다.");

        // [4] 북마크 해제 (Toggle OFF)
        mockMvc.perform(post("/api/bookmarks/toggle")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(false));

        // DB 정합성 검증: 실제 bookmark 테이블에 레코드가 지워졌는지 확인
        int countAfterRemove = bookmarkMapper.isBookmarked(testPostId, testUserId);
        assertEquals(0, countAfterRemove, "북마크 해제 후 DB 레코드가 삭제되어 있어야 합니다.");

        // [5] 상세페이지 조회 시 isBookmarked 가 false로 바인딩되는지 검증
        MvcResult detailResultAfterRemove = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isBookmarked"))
                .andReturn();

        Boolean isBookmarkedAfterRemove = (Boolean) detailResultAfterRemove.getModelAndView().getModel().get("isBookmarked");
        assertNotNull(isBookmarkedAfterRemove);
        assertFalse(isBookmarkedAfterRemove, "북마크 해제 상태에서 상세페이지 진입 시 isBookmarked는 false여야 합니다.");
    }
}

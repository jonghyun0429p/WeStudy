package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.like.mapper.LikeMapper;
import com.westudy.like.service.LikeService;
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
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private LikeService likeService;

    private Cookie[] authCookies;
    private long testUserId;
    private long testPostId;
    private long testCommentId;

    @BeforeEach
    void setupUserAndData() throws Exception {
        // 1. 기존 유저 생성 및 로그인
        String email = "liketestuser@naver.com";
        User existing = userMapper.findByEmail(email);
        if (existing == null) {
            User testUser = User.builder()
                    .username("liketestuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(email)
                    .nickname("좋아요테스터")
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

        // 2. 테스트용 게시글 및 본문 생성
        testPostId = insertPost(testUserId, "테스트 좋아요 대상 글");

        // 3. 테스트용 댓글 생성
        testCommentId = insertComment(testUserId, testPostId, "테스트 좋아요 대상 댓글");
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

        jdbcTemplate.update("INSERT INTO post_content (post_id, content) VALUES (?, '테스트 게시글 본문')", postId);
        return postId;
    }

    private long insertComment(long userId, long postId, String content) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                "INSERT INTO comment (user_id, post_id, content, created_at) VALUES (?, ?, ?, NOW())",
                java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, userId);
            ps.setLong(2, postId);
            ps.setString(3, content);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Test
    @DisplayName("게시글 좋아요 토글 및 상세페이지 모델 연동 통합 테스트")
    void testPostLikeToggleAndDetailModelBinding() throws Exception {
        // [1] 초기 상태 검증: 좋아요 개수 0
        int initialCount = likeService.getPostLikeCount(testPostId);
        assertEquals(0, initialCount, "초기 게시글 좋아요 수는 0이어야 합니다.");
        assertFalse(likeMapper.isPostLiked(testPostId, testUserId), "초기에는 좋아요 누른 상태가 아니어야 합니다.");

        // [2] 게시글 좋아요 누르기 (Toggle ON)
        mockMvc.perform(post("/api/like/post")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPostId)))
                .andExpect(status().isOk());

        // DB 정합성 검증: 좋아요 상태가 true이고 개수가 1로 증가했는지 확인
        assertTrue(likeMapper.isPostLiked(testPostId, testUserId), "좋아요 등록 후 상태가 true로 전환되어야 합니다.");
        assertEquals(1, likeService.getPostLikeCount(testPostId), "좋아요 등록 후 개수가 1이어야 합니다.");

        // [3] 상세페이지 조회 시 isLiked가 true이고 likeCount가 1인지 연동 검증
        MvcResult detailResultAfterLike = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isLiked"))
                .andExpect(model().attributeExists("likeCount"))
                .andReturn();

        Boolean isLikedAfterLike = (Boolean) detailResultAfterLike.getModelAndView().getModel().get("isLiked");
        Integer likeCountAfterLike = (Integer) detailResultAfterLike.getModelAndView().getModel().get("likeCount");
        assertTrue(isLikedAfterLike);
        assertEquals(1, likeCountAfterLike);

        // [4] 게시글 좋아요 취소하기 (Toggle OFF)
        mockMvc.perform(post("/api/like/notLikePost")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPostId)))
                .andExpect(status().isOk());

        // DB 정합성 검증: 좋아요 상태가 false이고 개수가 0으로 감소했는지 확인
        assertFalse(likeMapper.isPostLiked(testPostId, testUserId), "좋아요 취소 후 상태가 false여야 합니다.");
        assertEquals(0, likeService.getPostLikeCount(testPostId), "좋아요 취소 후 개수가 0이어야 합니다.");

        // [5] 상세페이지 조회 시 isLiked가 false이고 likeCount가 0인지 연동 검증
        MvcResult detailResultAfterUnlike = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isLiked"))
                .andExpect(model().attributeExists("likeCount"))
                .andReturn();

        Boolean isLikedAfterUnlike = (Boolean) detailResultAfterUnlike.getModelAndView().getModel().get("isLiked");
        Integer likeCountAfterUnlike = (Integer) detailResultAfterUnlike.getModelAndView().getModel().get("likeCount");
        assertFalse(isLikedAfterUnlike);
        assertEquals(0, likeCountAfterUnlike);
    }

    @Test
    @DisplayName("댓글 좋아요 토글 및 상세페이지 댓글 DTO 연동 통합 테스트")
    void testCommentLikeToggleAndDetailModelBinding() throws Exception {
        // [1] 초기 상태 검증: 좋아요 개수 0
        int initialCount = likeService.getCommentCount(testCommentId);
        assertEquals(0, initialCount, "초기 댓글 좋아요 수는 0이어야 합니다.");
        assertFalse(likeMapper.isCommentLiked(testCommentId, testUserId), "초기에는 댓글 좋아요 누른 상태가 아니어야 합니다.");

        // [2] 댓글 좋아요 누르기 (Toggle ON)
        mockMvc.perform(post("/api/like/comment")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentId)))
                .andExpect(status().isOk());

        // DB 정합성 검증: 좋아요 상태가 true이고 개수가 1로 증가했는지 확인
        assertTrue(likeMapper.isCommentLiked(testCommentId, testUserId), "좋아요 등록 후 댓글 상태가 true로 전환되어야 합니다.");
        assertEquals(1, likeService.getCommentCount(testCommentId), "좋아요 등록 후 댓글 개수가 1이어야 합니다.");

        // [3] 상세페이지 조회 시 댓글 리스트 DTO에 isLiked가 true이고 likeCount가 1로 연동되었는지 확인
        MvcResult detailResultAfterLike = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("comments"))
                .andReturn();

        List<?> commentsAfterLike = (List<?>) detailResultAfterLike.getModelAndView().getModel().get("comments");
        assertNotNull(commentsAfterLike);
        assertFalse(commentsAfterLike.isEmpty());

        // DTO 내부 값 형변환 및 직접 비교 검증
        com.westudy.comment.dto.CommentResponseDTO commentDto = (com.westudy.comment.dto.CommentResponseDTO) commentsAfterLike.get(0);
        assertTrue(commentDto.isLiked());
        assertEquals(1, commentDto.getLikeCount());

        // [4] 댓글 좋아요 취소하기 (Toggle OFF)
        mockMvc.perform(post("/api/like/notLikeComment")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentId)))
                .andExpect(status().isOk());

        // DB 정합성 검증: 좋아요 상태가 false이고 개수가 0으로 감소했는지 확인
        assertFalse(likeMapper.isCommentLiked(testCommentId, testUserId), "좋아요 취소 후 댓글 상태가 false여야 합니다.");
        assertEquals(0, likeService.getCommentCount(testCommentId), "좋아요 취소 후 댓글 개수가 0이어야 합니다.");

        // [5] 상세페이지 조회 시 댓글 리스트 DTO에 isLiked가 false이고 likeCount가 0으로 연동되었는지 확인
        MvcResult detailResultAfterUnlike = mockMvc.perform(get("/page/post/detail")
                        .cookie(authCookies)
                        .param("id", String.valueOf(testPostId)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("comments"))
                .andReturn();

        List<?> commentsAfterUnlike = (List<?>) detailResultAfterUnlike.getModelAndView().getModel().get("comments");
        assertNotNull(commentsAfterUnlike);
        assertFalse(commentsAfterUnlike.isEmpty());

        com.westudy.comment.dto.CommentResponseDTO commentDtoAfterUnlike = (com.westudy.comment.dto.CommentResponseDTO) commentsAfterUnlike.get(0);
        assertFalse(commentDtoAfterUnlike.isLiked());
        assertEquals(0, commentDtoAfterUnlike.getLikeCount());
    }
}

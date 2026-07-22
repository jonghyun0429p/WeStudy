package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.comment.dto.CommentDeleteRequest;
import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentResponseDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.comment.mapper.CommentMapper;
import com.westudy.post.entity.Post;
import com.westudy.post.enums.PostCategory;
import com.westudy.post.mapper.PostMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional // 각 테스트 완료 후 DB 변경사항 롤백 처리 활성화
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(CommentControllerTest.class);

    private Cookie[] authCookies;
    private Long dynamicPostId;
    private Long testUserId;

    @BeforeEach
    void setupDatabaseAndLogin() throws Exception {
        // 1. 기존 컨테이너 스키마 호환을 위해 address 컬럼 존재 여부 체크 및 추가
        try {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS address VARCHAR(255) NULL");
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS latitude DOUBLE NULL");
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS longitude DOUBLE NULL");
        } catch (Exception e) {
            log.warn("게시글 테이블 컬럼 추가 실패 혹은 이미 존재함: {}", e.getMessage());
        }

        // 2. 테스트용 유저 생성 (없을 경우에만 추가)
        String testEmail = "testuser@naver.com";
        User existingUser = userMapper.findByEmail(testEmail);
        if (existingUser == null) {
            User newUser = User.builder()
                    .username("testuser")
                    .password("$2a$10$8pEKjjYcblixkJYIHwK3mOjCw7m/XSiHPl2H.hKiVsOjI2B1nc8mS") // testpassword
                    .email(testEmail)
                    .nickname("테스트닉네임")
                    .phoneNumber("010-0000-0000")
                    .role(UserRole.ROLE_USER)
                    .build();
            userMapper.insertUser(newUser);
            testUserId = newUser.getId();
        } else {
            testUserId = existingUser.getId();
        }

        // 3. 테스트용 게시글 생성 ( Mybatis Key 생성 기능 활용 )
        Post testPost = Post.builder()
                .userId(testUserId)
                .views(0L)
                .isNotice(false)
                .category(PostCategory.FREE)
                .title("통합 테스트용 게시글 제목")
                .summary("테스트 본문 요약")
                .createAt(LocalDateTime.now())
                .build();
        postMapper.insertPost(testPost);
        dynamicPostId = testPost.getId();

        // 4. 로그인 수행하여 쿠키 획득
        UserLoginDTO loginDto = new UserLoginDTO();
        loginDto.setEmail(testEmail);
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
    @DisplayName("댓글 라이프사이클 통합 테스트 - 등록, 조회, 수정, 삭제의 전체 DB 동작 검증")
    void testCommentLifecycle() throws Exception {
        // [1] 댓글 등록 API 호출
        CommentInsertDTO insertDto = new CommentInsertDTO();
        insertDto.setPostId(dynamicPostId);
        insertDto.setContent("첫 통합 테스트 댓글");

        mockMvc.perform(post("/api/comments/create")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect_url").value("/post/detail?id=" + dynamicPostId));

        // DB 검증: 실제로 댓글이 데이터베이스에 등록되었는지 확인
        List<CommentResponseDTO> commentsAfterInsert = commentMapper.findCommentsByPostId(dynamicPostId, null);
        assertEquals(1, commentsAfterInsert.size(), "DB에 댓글이 1개 적재되어야 합니다.");
        CommentResponseDTO createdComment = commentsAfterInsert.get(0);
        assertEquals("첫 통합 테스트 댓글", createdComment.getContent(), "DB에 저장된 댓글 내용이 일치해야 합니다.");
        long savedCommentId = createdComment.getCommentId();

        // [2] 댓글 조회 API 호출 및 응답 데이터 검증
        mockMvc.perform(post("/api/comments/post")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dynamicPostId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(savedCommentId))
                .andExpect(jsonPath("$[0].content").value("첫 통합 테스트 댓글"));

        // [3] 댓글 수정 API 호출
        CommentUpdateDTO updateDto = new CommentUpdateDTO();
        updateDto.setId(savedCommentId);
        updateDto.setPostId(dynamicPostId);
        updateDto.setContent("수정된 통합 테스트 댓글");

        mockMvc.perform(post("/api/comments/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect_url").value("/post/detail?id=" + dynamicPostId));

        // DB 검증: 실제로 데이터베이스 상의 댓글 내용이 수정되었는지 확인
        List<CommentResponseDTO> commentsAfterUpdate = commentMapper.findCommentsByPostId(dynamicPostId, null);
        assertEquals(1, commentsAfterUpdate.size());
        assertEquals("수정된 통합 테스트 댓글", commentsAfterUpdate.get(0).getContent(), "DB의 댓글 내용이 수정 완료 상태여야 합니다.");

        // [4] 댓글 삭제 API 호출
        CommentDeleteRequest deleteDto = new CommentDeleteRequest(savedCommentId);
        mockMvc.perform(post("/api/comments/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect_url").value("/post"));

        // DB 검증: 실제로 데이터베이스 상에서 댓글이 소프트 딜리트(삭제 처리) 되었는지 확인
        List<CommentResponseDTO> commentsAfterDelete = commentMapper.findCommentsByPostId(dynamicPostId, null);
        assertTrue(commentsAfterDelete.isEmpty(), "삭제 처리 후 액티브 댓글 조회 목록은 비어있어야 합니다.");
    }
}

package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.post.dto.PostInsertDTO;
import com.westudy.post.dto.PostUpdateDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional // 테스트 완료 후 자동으로 DB 롤백 처리 활성화
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(PostControllerTest.class);

    private Cookie[] authCookies;
    private Long testUserId;

    @BeforeEach
    void setupDatabaseAndLogin() throws Exception {
        // 1. 기존 컨테이너 DB 컬럼 정합성을 위해 address 관련 컬럼 추가
        try {
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS address VARCHAR(255) NULL");
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS latitude DOUBLE NULL");
            jdbcTemplate.execute("ALTER TABLE post ADD COLUMN IF NOT EXISTS longitude DOUBLE NULL");
        } catch (Exception e) {
            log.warn("게시글 테이블 컬럼 추가 실패 혹은 이미 존재함: {}", e.getMessage());
        }

        // 2. 테스트 회원 생성 (없을 경우에만 추가)
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

        // 3. 로그인 수행 후 인증 쿠키 획득
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
    @DisplayName("게시글 라이프사이클 통합 테스트 - 등록, 조회, 수정, 삭제의 전체 DB 동작 검증")
    void testPostLifecycle() throws Exception {
        // [1] 게시글 등록
        log.info("게시글 등록 테스트 시작.");
        PostInsertDTO insertDto = new PostInsertDTO();
        insertDto.setTitle("통합 테스트 쿠키 글");
        insertDto.setContent("본문입니다");
        insertDto.setPostCategory(PostCategory.FREE);
        insertDto.setNotice(false);

        mockMvc.perform(post("/api/post/insert")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertDto)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증 및 생성된 게시글 ID 획득
        List<Post> posts = postMapper.findByUserId(testUserId);
        assertFalse(posts.isEmpty(), "DB에 저장된 유저의 게시글 목록이 비어있지 않아야 합니다.");
        Post createdPost = posts.get(posts.size() - 1); // 가장 최신 등록 게시글
        long savedPostId = createdPost.getId();
        assertEquals("통합 테스트 쿠키 글", createdPost.getTitle(), "게시글 제목이 DB에 정상 등록되었는지 검증");

        // [2] 게시글 상세 조회 (SSR)
        mockMvc.perform(get("/page/post/detail")
                        .param("id", String.valueOf(savedPostId))
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(view().name("/layout/post/detail"))
                .andExpect(model().attributeExists("page"));

        // [3] 게시글 수정
        PostUpdateDTO updateDto = new PostUpdateDTO();
        updateDto.setPostId(savedPostId);
        updateDto.setTitle("수정된 통합 제목");
        updateDto.setContent("수정된 통합 본문");
        updateDto.setCategory(PostCategory.QNA);

        mockMvc.perform(post("/api/post/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: 제목 수정 여부 확인
        Post updatedPost = postMapper.findByPostId(savedPostId);
        assertEquals("수정된 통합 제목", updatedPost.getTitle(), "수정 사항이 DB에 저장되어야 합니다.");

        // [4] 게시글 삭제 (soft delete)
        mockMvc.perform(post("/api/post/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(savedPostId)))
                .andExpect(status().is2xxSuccessful());

        // DB 검증: soft delete 처리 확인 (delete_at 필드가 null이 아님)
        Post deletedPost = postMapper.findByPostId(savedPostId);
        assertNotNull(deletedPost.getDeleteAt(), "삭제 완료 후 deleteAt 필드가 채워져 소프트 딜리트 처리되어야 합니다.");
    }
}

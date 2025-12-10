package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.post.dto.PostInsertDTO;
import com.westudy.post.dto.PostUpdateDTO;
import com.westudy.post.enums.PostCategory;
import com.westudy.user.dto.UserLoginDTO;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(PostControllerTest.class);

    private static Cookie[] authCookies;
    private static Long savedPostId;

    @BeforeEach
    void loginAndSaveCookies() throws Exception {
        if (authCookies == null) { // 최초 1회만 로그인
            UserLoginDTO dto = new UserLoginDTO();
            dto.setEmail("testuser@naver.com");
            dto.setPassword("testpassword");

            authCookies = mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getCookies();
        }


    }

    @Test
    @Order(1)
    @DisplayName("게시글 등록")
    void insertPost() throws Exception {
        log.info("게시글 등록 테스트 시작.");
        PostInsertDTO dto = new PostInsertDTO();
        dto.setTitle("쿠키 테스트 글");
        dto.setContent("본문입니다");
        dto.setPostCategory(PostCategory.FREE);
        dto.setNotice(false);

        try {
            mockMvc.perform(post("/api/post/insert")
                            .cookie(authCookies)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().is2xxSuccessful());
        } catch (Exception e) {
            e.printStackTrace(); // 전체 MyBatis 예외 로그 출력
            throw e;
        }

        // postId 저장은 생략 (보통은 응답에서 받도록 설계 개선)
        savedPostId = 1L;
    }

    @Test
    @Order(2)
    @DisplayName("게시글 상세 조회 (SSR)")
    void detailPage() throws Exception {
        mockMvc.perform(get("/page/post/detail")
                        .param("id", String.valueOf(8L))
                        .cookie(authCookies))
                .andExpect(status().isOk())
                .andExpect(view().name("/layout/post/detail"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @Order(3)
    @DisplayName("게시글 수정")
    void updatePost() throws Exception {
        PostUpdateDTO dto = new PostUpdateDTO();
        dto.setPostId(8L);
        dto.setTitle("수정된 제목");
        dto.setContent("수정된 본문");
        dto.setCategory(PostCategory.QNA);

        mockMvc.perform(post("/api/post/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(4)
    @DisplayName("게시글 삭제 (soft delete)")
    void deletePost() throws Exception {
        mockMvc.perform(post("/api/post/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(8L)))
                .andExpect(status().is2xxSuccessful());
    }
}

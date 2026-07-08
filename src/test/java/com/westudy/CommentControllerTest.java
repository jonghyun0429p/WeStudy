package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.comment.dto.CommentDeleteRequest;
import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.user.dto.UserLoginDTO;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(CommentControllerTest.class);

    private static Cookie[] authCookies;
    private static Long savedCommentId;
    private static final Long TEST_POST_ID = 1L; // 테스트용 게시글 ID

    @BeforeEach
    void loginAndPostingOnce() throws Exception {
        if (authCookies == null) {
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
    @DisplayName("댓글 작성")
    void insertComment() throws Exception {
        CommentInsertDTO dto = new CommentInsertDTO();
        dto.setPostId(TEST_POST_ID);
        dto.setContent("테스트 댓글입니다.");

        MvcResult result = mockMvc.perform(post("/api/comments/create")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect_url").value("/post/detail?id=" + TEST_POST_ID))
                .andReturn(); // ✅ 여기서 ResultActions → MvcResult로 변환

        String responseBody = result.getResponse().getContentAsString();
        log.info("📌 Response Body: {}", responseBody);

        // 실제 테스트 환경에서는 댓글 ID를 DB 조회로 가져오는 로직이 필요할 수 있음
        savedCommentId = 1L; // 예시로 고정
    }

    @Test
    @Order(2)
    @DisplayName("댓글 조회")
    void findCommentByPostId() throws Exception {
        mockMvc.perform(post("/api/comments/post")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TEST_POST_ID)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        CommentUpdateDTO dto = new CommentUpdateDTO();
        dto.setId(savedCommentId);
        dto.setPostId(TEST_POST_ID);
        dto.setContent("수정된 댓글 내용입니다.");

        mockMvc.perform(post("/api/comments/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()) // ✅ 3xx → 200 변경
                .andExpect(jsonPath("$.redirect_url")
                        .value("/post/detail?id=" + TEST_POST_ID));
    }

    @Test
    @Order(4)
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        CommentDeleteRequest deleteDto = new CommentDeleteRequest(savedCommentId);
        mockMvc.perform(post("/api/comments/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteDto)))
                .andExpect(status().isOk()) // ✅ 3xx → 200 변경
                .andExpect(jsonPath("$.redirect_url").value("/post"));
    }

}

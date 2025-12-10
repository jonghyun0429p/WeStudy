package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.dto.StudyUpdateDTO;
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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback
public class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(StudyControllerTest.class);

    private static Cookie[] authCookies;
    private static Long savedStudyId = 1L; // 임시 ID

    @BeforeEach
    void loginOnce() throws Exception {
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
    @DisplayName("스터디 등록")
    void insertStudy() throws Exception {
        StudyInsertDTO dto = new StudyInsertDTO();
        dto.setTitle("테스트 스터디");
        dto.setLocation("서울");
        dto.setMaxMember(5);

        try{
            mockMvc.perform(post("/api/study/insert")
                            .cookie(authCookies)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().is2xxSuccessful());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Test
    @Order(2)
    @DisplayName("스터디 수정")
    void updateStudy() throws Exception {
        StudyUpdateDTO dto = new StudyUpdateDTO();
        dto.setId(savedStudyId);
        dto.setTitle("수정된 스터디 제목");
        dto.setLocation("서울");
        dto.setMaxMember(10);

        mockMvc.perform(post("/api/study/update")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(3)
    @DisplayName("스터디 삭제")
    void deleteStudy() throws Exception {
        mockMvc.perform(post("/api/study/delete")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(savedStudyId)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(4)
    @DisplayName("스터디 신청")
    void applicationStudy() throws Exception {
        long studyId = 8;
        System.out.println("📌 studyId: " + studyId);

        mockMvc.perform(post("/api/study/application")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyId)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("스터디 중복 신청")
    void applicationDuplicationStudy() throws Exception{
        long studyId = 8;

        mockMvc.perform(post("/api/study/application")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyId)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(6)
    @DisplayName("스터디 승인")
    void approveStudy() throws Exception{

        Map<String, Object> body = new HashMap<>();
        body.put("studyId", 17L);
        body.put("userId", 3L);
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/study/approve")
                .cookie(authCookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(7)
    @DisplayName("스터디 거부")
    void rejectStudy() throws Exception{
        Map<String, Object> body = new HashMap<>();
        body.put("studyId", 17L);
        body.put("userId", 2L);
        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/study/reject")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(8)
    @DisplayName("스터디 취소")
    void cancelApplication() throws Exception{
        long studyId = 8;

        mockMvc.perform(post("/api/study/cancel")
                        .cookie(authCookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyId)))
                .andExpect(status().is2xxSuccessful());
    }
}

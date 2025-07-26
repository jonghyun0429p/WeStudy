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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        dto.setLocation("부산");
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
}

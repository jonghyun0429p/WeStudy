package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Test
    void httpRequestSignup() throws Exception {
        log.info("User 생성");
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("signupuser");
        userDTO.setPassword("testpassword");
        userDTO.setEmail("signupuser@naver.com");
        userDTO.setNickname("signuptester");
        userDTO.setPhoneNumber("010-1234-5678");

        String jsonRequest = objectMapper.writeValueAsString(userDTO);

        log.info(jsonRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirect_url").value("/login"));

        // DB 검증: 실제로 유저가 데이터베이스에 올바르게 적재되었는지 확인
        com.westudy.user.entity.User createdUser = userMapper.findByEmail("signupuser@naver.com");
        assertNotNull(createdUser, "회원가입 완료 후 DB에 유저가 등록되어 있어야 합니다.");
        assertEquals("signupuser", createdUser.getUsername(), "아이디 검증");
        assertEquals("signuptester", createdUser.getNickname(), "닉네임 검증");
        assertEquals("010-1234-5678", createdUser.getPhoneNumber(), "전화번호 검증");
        assertEquals(com.westudy.user.enums.UserRole.ROLE_USER, createdUser.getRole(), "기본 권한 검증");
    }

}

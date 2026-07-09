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
    }

}

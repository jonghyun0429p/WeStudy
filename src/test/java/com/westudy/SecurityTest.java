package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserLoginDTO;
import com.westudy.user.entity.User;
import com.westudy.user.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class SecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @BeforeEach
    void clean() throws Exception {
        User user = userMapper.findByUsername("testuser");
        if(user == null){
            log.info("UserDTO 생성");
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("testUser");
            userDTO.setPassword("testpassword");
            userDTO.setEmail("testuser@naver.com");
            userDTO.setNickname("tester");
            userDTO.setPhoneNumber("010-1234-5678");

            String jsonSignupRequest = objectMapper.writeValueAsString(userDTO);
            log.info(jsonSignupRequest);

            //회원가입
            mockMvc.perform(MockMvcRequestBuilders.post("/users/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonSignupRequest))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));

        }
    }

    @Test
    void httpRequestLogin() throws Exception {
        //로그인
        log.info("로그인 DTO 생성");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("testuser@naver.com");
        userLoginDTO.setPassword("testpassword");

        String jsonLoginRequest = objectMapper.writeValueAsString(userLoginDTO);
        log.info("로그인 실행");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLoginRequest))
                .andExpect(status().isOk());

    }

    @Test
    void adminPage() throws Exception{
        //admin 존재.

        //admin 로그인 DTO 생성
        log.info("admin 로그인 DTO 생성");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("adminuser@naver.com");
        userLoginDTO.setPassword("adminpassword");

        String jsonLoginRequest = objectMapper.writeValueAsString(userLoginDTO);
        log.info("로그인 실행");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonLoginRequest))
                                    .andExpect(status().isOk())
                                    .andReturn();

        String response = result.getResponse().getContentAsString();
        log.info("로그인 응답: {}", response);

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);

        String redirectUrl = (String) responseMap.get("redirect_url");
        log.info("리디렉트 URL: {}", redirectUrl);

        // 검증 가능
        Assertions.assertEquals("/admin", redirectUrl); // 관리자면 "/admin" 기대
    }
}

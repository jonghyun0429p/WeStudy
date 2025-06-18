package com.westudy.user.intitialize;

import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userMapper.findByUsername("testuser");
        if (admin == null) {
            User newUser = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("testpassword"))
                    .email("testuser@naver.com")
                    .nickname("테스터")
                    .phoneNumber("010-0000-0000")
                    .role(UserRole.ROLE_USER)
                    .build();

            userMapper.insertUser(newUser);

            System.out.println("test 계정 자동 생성 완료");
        }
    }
}

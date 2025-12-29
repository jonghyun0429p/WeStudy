package com.westudy.user.intitialize;

import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class TestUserInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User tUser = userMapper.findByUsername("testuser");
        if (tUser == null) {

            for(int i = 0; i < 10; i++){
                User newUser = User.builder()
                        .username("testuser"+i)
                        .password(passwordEncoder.encode("testpassword"))
                        .email("testuser"+i+"@naver.com")
                        .nickname("테스터"+i)
                        .phoneNumber("010-0000-000"+i)
                        .role(UserRole.ROLE_USER)
                        .build();

                userMapper.insertUser(newUser);
            }


            System.out.println("test 계정 자동 생성 완료");
        }
    }
}

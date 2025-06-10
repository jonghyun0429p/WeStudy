package com.westudy.admin.initializer;

import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userMapper.findByUsername("adminuser");
        if (admin == null) {
            User newAdmin = User.builder()
                    .username("adminuser")
                    .password(passwordEncoder.encode("adminpassword"))
                    .email("adminuser@naver.com")
                    .nickname("관리자")
                    .phoneNumber("010-0000-0000")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            userMapper.insertUser(newAdmin);

            System.out.println("Admin 계정 자동 생성 완료");
        }
    }
}

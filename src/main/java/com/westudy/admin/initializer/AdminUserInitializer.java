package com.westudy.admin.initializer;

import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("docker")
public class AdminUserInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.bootstrap-enabled:false}")
    private boolean enabled;

    @Value("${app.admin.username:}")
    private String username;

    @Value("${app.admin.password:}")
    private String rawPassword;

    @Value("${app.admin.email:}")
    private String email;


    @Override
    public void run(String... args) {
        // 1) docker 프로필이어도 enabled=false면 아무것도 안 함
        if (!enabled) return;

        // 2) enabled=true인데 필수값 비어있으면 부팅 실패로 “실수”를 빨리 잡음
        if (username.isBlank() || rawPassword.isBlank() || email.isBlank()) {
            throw new IllegalStateException("Admin bootstrap enabled but APP_ADMIN_* env values are missing.");
        }

        // 3) 멱등 처리: 이미 있으면 재생성/덮어쓰기 안 함
        User exist = userMapper.findByUsername(username);
        if (exist != null) return;

        User newAdmin = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .nickname("관리자")
                .phoneNumber("010-0000-0000")
                .role(UserRole.ROLE_ADMIN)
                .build();

        userMapper.insertUser(newAdmin);

        System.out.println("Admin 계정 1회 생성 완료");
    }
}

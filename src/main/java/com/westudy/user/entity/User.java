package com.westudy.user.entity;

import com.westudy.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private UserRole role;
}

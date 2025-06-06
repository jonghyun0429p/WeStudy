package com.westudy.user.entity;

import com.westudy.user.dto.UserDTO;
import com.westudy.user.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private UserRole role;
}

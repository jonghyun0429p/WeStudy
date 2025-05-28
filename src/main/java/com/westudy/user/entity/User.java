package com.westudy.user.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class User {
    private Long userId;
    private String userName;
    private String password;
    private String email;
    private String nickName;
    private String phoneNumber;
    private LocalDateTime createdAt;
}

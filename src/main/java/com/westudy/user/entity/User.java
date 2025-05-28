package com.westudy.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String userName;
    private String password;
    private String email;
    private String nickName;
    private String phoneNumber;
    private LocalDateTime createdAt;
}

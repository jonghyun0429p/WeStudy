package com.westudy.user.entity;

import com.westudy.user.dto.UserDTO;
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
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;

    User(UserDTO userDto){
        this.userName = userDto.getUserName();
        this.password = userDto.getPassword();
        this.email = userDto.getEmail();
        this.nickname = userDto.getNickname();
        this.phoneNumber = userDto.getPhoneNumber();
        this.createdAt = LocalDateTime.now();
    }
}

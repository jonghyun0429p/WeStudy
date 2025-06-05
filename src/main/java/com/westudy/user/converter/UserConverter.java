package com.westudy.user.converter;

import com.westudy.user.dto.UserDTO;
import com.westudy.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserConverter {
    public User toEntity(UserDTO userDTO){
        return User.builder()
                .userName(userDTO.getUserName())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .phoneNumber(userDTO.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }
}

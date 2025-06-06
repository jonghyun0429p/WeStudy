package com.westudy.user.converter;

import com.westudy.user.dto.UserDTO;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserConverter {
    public User toEntity(UserDTO userDTO){
        return User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .phoneNumber(userDTO.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .role(UserRole.ROLE_USER)
                .build();
    }
}

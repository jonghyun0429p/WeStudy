package com.westudy.user.converter;

import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserEditDTO;
import com.westudy.user.dto.UserInfoDTO;
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
    public UserEditDTO toUserEdit(User user){
        UserEditDTO dto = new UserEditDTO();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }

    public UserInfoDTO toUserInfo(User user){
        UserInfoDTO dto = new UserInfoDTO();
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }
}

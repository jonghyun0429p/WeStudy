package com.westudy.user.dto;

import com.westudy.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditDTO {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phoneNumber;

    public static UserEditDTO from(User user){
        UserEditDTO dto = new UserEditDTO();
        dto.username = user.getUsername();
        dto.password = user.getPassword();
        dto.nickname = user.getNickname();
        dto.email = user.getEmail();
        dto.phoneNumber = user.getPhoneNumber();

        return dto;
    }
}

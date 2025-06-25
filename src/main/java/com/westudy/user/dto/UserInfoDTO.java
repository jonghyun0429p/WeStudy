package com.westudy.user.dto;

import com.westudy.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO {
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;

    public static UserInfoDTO from(User user){
        UserInfoDTO dto = new UserInfoDTO();
        dto.username = user.getUsername();
        dto.nickname = user.getNickname();
        dto.email = user.getEmail();
        dto.phoneNumber = user.getPhoneNumber();

        return dto;
    }
}

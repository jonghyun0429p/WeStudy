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
}

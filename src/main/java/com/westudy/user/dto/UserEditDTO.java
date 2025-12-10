package com.westudy.user.dto;

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
}

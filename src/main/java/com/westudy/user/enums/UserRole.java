package com.westudy.user.enums;

import com.westudy.global.exception.BaseException;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    public static UserRole from(String value) {
        try {
            return UserRole.valueOf(value);
        } catch (Exception e) {
            throw new BaseException(UserErrorCode.USER_ROLE_ERROR); // 나중에 에러 처리가 필요하면 적용하는걸로.
        }
    }
}
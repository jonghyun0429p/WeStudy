package com.westudy.user.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum UserErrorCode implements BaseErrorCode {
    USER_USERNAME_DUPLICATE(HttpServletResponse.SC_BAD_REQUEST, "유저 아이디가 존재합니다."),
    USER_USERNAME_UNEXITED(HttpServletResponse.SC_BAD_REQUEST, "유저 아이디가 없습니다."),
    USER_ROLE_ERROR(HttpServletResponse.SC_BAD_REQUEST, "유저 권한이 없습니다"),
    USER_EMPTY(HttpServletResponse.SC_NOT_FOUND, "유저를 찾을 수 없습니다.");

    private final int httpStatus;
    private final String message;


    UserErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus(){
        return httpStatus;
    }

    public String getMessage(){
        return message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}

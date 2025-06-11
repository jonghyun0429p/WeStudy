package com.westudy.user.exception;

import jakarta.servlet.http.HttpServletResponse;

public enum UserErrorCode {
    USER_USERNAME_DUPLICATE(HttpServletResponse.SC_BAD_REQUEST, "유저 아이디가 존재합니다."),
    USER_USERNAME_UNEXITED(HttpServletResponse.SC_BAD_REQUEST, "유저 아이디가 없습니다.");

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
}

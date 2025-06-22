package com.westudy.security.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum SecurityErrorCode implements BaseErrorCode {
    AUTHENTICATION_EMPTY(HttpServletResponse.SC_UNAUTHORIZED, "Authentication 추출 안됨");

    private final int httpStatus;
    private final String message;

    SecurityErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}


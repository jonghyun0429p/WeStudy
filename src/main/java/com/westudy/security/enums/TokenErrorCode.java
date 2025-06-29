package com.westudy.security.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum TokenErrorCode implements BaseErrorCode {
    EXPIRED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "토큰 구조가 올바르지 않습니다."),
    MISSING_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    COOKIE_NOT_HAVE_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "쿠키에 토큰이 존재하지 않습니다."),
    UNISSUED_TOKEN(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 발급되지 않았습니다."),
    NOT_SAME_REFRESHTOKEN(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 일치하지 않습니다."),
    JWT_FAILED(HttpServletResponse.SC_UNAUTHORIZED, "JWT 인증이 실패했습니다.");

    private final String message;
    private final int httpStatus;

    private TokenErrorCode(int httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    public int getHttpStatus() { return httpStatus; }
}

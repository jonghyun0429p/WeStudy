package com.westudy.post.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum PostErrorCode implements BaseErrorCode {
    POST_NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "게시글이 없습니다,"),
    POST_ID_NOT_SAME(HttpServletResponse.SC_BAD_REQUEST, "작성자와 일치하지 않습니다.");

    private final int httpStatus;
    private final String message;

    PostErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public int getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}

package com.westudy.comment.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum CommentErrorCode implements BaseErrorCode {
    COMMENT_NOT_WRITER(HttpServletResponse.SC_UNAUTHORIZED, "작성자가 아닙니다."),
    COMMENT_NOT_EXIST(HttpServletResponse.SC_BAD_REQUEST, "댓글이 존재하지 않습니다.");

    private int httpstatus;
    private String message;

    CommentErrorCode(int httpstatus, String message) {
        this.httpstatus = httpstatus;
        this.message = message;

    }

    @Override
    public int getHttpStatus() {
        return this.httpstatus;
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

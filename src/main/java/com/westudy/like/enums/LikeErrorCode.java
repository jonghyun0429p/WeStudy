package com.westudy.like.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum LikeErrorCode implements BaseErrorCode {
    LIKE_NOT_EXIST_POST_ID(HttpServletResponse.SC_BAD_REQUEST, "없는 게시글 추천 시도"),
    LIKE_NOT_EXIST_COMMENT_ID(HttpServletResponse.SC_BAD_REQUEST, "없는 댓글 추천 시도");

    private int httpstatus;
    private String message;

    LikeErrorCode(int httpstatus, String message) {
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

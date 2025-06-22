package com.westudy.global.enums;

import jakarta.servlet.http.HttpServletResponse;

public enum PageErrorCode implements BaseErrorCode{
    PAGE_MISSING(HttpServletResponse.SC_NOT_FOUND, "페이지가 없습니다.");

    private final int httpStatus;
    private final String message;

    PageErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode(){ return this.getCode(); }
}

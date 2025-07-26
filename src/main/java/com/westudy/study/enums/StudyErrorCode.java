package com.westudy.study.enums;

import com.westudy.global.enums.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;

public enum StudyErrorCode implements BaseErrorCode {
    STUDY_EMPTY(HttpServletResponse.SC_NOT_FOUND, "스터디가 없습니다."),
    STUDY_FULL(HttpServletResponse.SC_BAD_REQUEST, "스터디가 이미 가득 차있습니다."),
    STUDY_NOT_WRITER(HttpServletResponse.SC_BAD_REQUEST, "스터디의 작성자가 아닙니다.");

    private final int httpStatus;
    private final String message;

    StudyErrorCode(int httpStatus, String message) {
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
    public String getCode() {
        return this.name();
    }
}

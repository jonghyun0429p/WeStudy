package com.westudy.global.enums;

public interface BaseErrorCode {
    int getHttpStatus();
    String getMessage();
    String getCode();
}

package com.westudy.global.exception;

import com.westudy.global.enums.BaseErrorCode;

public class BaseException extends RuntimeException{
    private final BaseErrorCode errorCode;

    public BaseException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode(){
        return errorCode;
    }

    public int getHttpStatus(){
        return errorCode.getHttpStatus();
    }
}

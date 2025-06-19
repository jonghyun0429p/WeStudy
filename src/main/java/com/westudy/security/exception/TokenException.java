package com.westudy.security.exception;

import com.westudy.security.enums.TokenErrorCode;

public class TokenException extends RuntimeException {
    private final TokenErrorCode errorCode;

    public TokenException(final TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TokenErrorCode getErrorCode() {
        return errorCode;
    }
}

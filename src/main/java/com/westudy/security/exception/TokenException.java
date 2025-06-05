package com.westudy.security.exception;

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

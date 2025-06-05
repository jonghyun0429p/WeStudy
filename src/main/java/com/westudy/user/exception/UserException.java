package com.westudy.user.exception;

public class UserException extends RuntimeException {
    private final UserErrorCode userErrorCode;

    public UserException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }

    public UserErrorCode getErrorCode() {
        return userErrorCode;
    }
}


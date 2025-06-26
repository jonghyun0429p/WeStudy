package com.westudy.security.exception;

import com.westudy.global.exception.BaseException;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    public JwtAuthenticationException(BaseException cause) {
        super(cause.getMessage());
        initCause(cause);
    }
}

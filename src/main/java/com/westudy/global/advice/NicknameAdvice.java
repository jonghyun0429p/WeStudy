package com.westudy.global.advice;

import com.westudy.security.entity.CustomUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NicknameAdvice {
    private static final Logger log = LoggerFactory.getLogger(NicknameAdvice.class);

    @ModelAttribute("nickname")
    public String nickname(@AuthenticationPrincipal CustomUserDetail user) {
        if (user != null) {
            log.info("로그인한 사용자 닉네임: {}", user.getUserNickname());
        } else {
            log.info("로그인하지 않은 사용자입니다.");
        }
        return (user != null) ? user.getUserNickname() : null;
    }
}

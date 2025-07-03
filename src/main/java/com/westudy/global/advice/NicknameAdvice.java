package com.westudy.global.advice;

import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NicknameAdvice {

    private static final Logger log = LoggerFactory.getLogger(NicknameAdvice.class);

    @ModelAttribute("nickname")
    public String nickname(HttpServletRequest request) {
        return SecurityUtil.resolveCurrentNicknameSafely();
    }
}
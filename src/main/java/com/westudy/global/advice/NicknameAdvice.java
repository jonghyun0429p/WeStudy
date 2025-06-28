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
        String accept = request.getHeader("Accept");

        // SSR 요청인 경우에만 nickname을 넣는다
        if (accept != null && accept.contains("text/html")) {
            String nickname = SecurityUtil.getCurrentNickname();
            log.debug("SSR 요청 - 로그인한 사용자 닉네임: {}", nickname);
            return nickname;
        }

        // API 요청이라면 nickname은 null 처리
        return null;
    }
}

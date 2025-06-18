package com.westudy.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.security.dto.TokenInfo;
import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.CustomUserDetailService;
import com.westudy.user.mapper.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public CustomAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        log.info("Authorities: " + customUserDetail.getAuthorities());

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(customUserDetail.getAuthorities(), customUserDetail.getUsername(), customUserDetail.getUserNickname());


        String redirectUrl;
        boolean isAdmin = customUserDetail.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            redirectUrl = "/admin";
        } else {
            redirectUrl = "/";
        }

        boolean isProduction = false; // 운영 여부 체크

        String cookieHeader = "access_token=" + tokenInfo.getAccess_token()
                + "; Path=/"
                + "; HttpOnly";

        if (isProduction) {
            cookieHeader += "; SameSite=None; Secure";
        } else {
            cookieHeader += "; SameSite=Lax"; // 개발용 테스트 시 Lax로 충분
        }

        response.setHeader("Set-Cookie", cookieHeader);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();

        var responseMap = new HashMap<String, Object>();
        responseMap.put("redirect_url", redirectUrl);

        String responseJson = objectMapper.writeValueAsString(responseMap);
        response.getWriter().write(responseJson);
    }

}

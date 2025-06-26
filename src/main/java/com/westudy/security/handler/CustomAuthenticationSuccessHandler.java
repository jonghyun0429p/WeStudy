package com.westudy.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.security.dto.TokenInfo;
import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.AuthService;
import com.westudy.security.service.CustomUserDetailService;
import com.westudy.user.mapper.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    private final long THIRTY_MINUTES = 60 * 30;
    private final long ONE_DAY = 60 * 60 * 24;

    @Value("${app.env}")
    private String environment;

    public CustomAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        log.info("Authorities: " + customUserDetail.getAuthorities());

        //토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(customUserDetail.getAuthorities(), customUserDetail.getUsername(),
                customUserDetail.getUserNickname(), customUserDetail.getUserId());

        log.info("리프레시 토큰 저장");
        authService.insertToken(customUserDetail.getUserId(), tokenInfo.getRefresh_token());

        String redirectUrl;
        boolean isAdmin = customUserDetail.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            redirectUrl = "/page/admin";
        } else {
            redirectUrl = "/";
        }

        //토큰 쿠키에 저장
        response.addHeader("Set-Cookie", authService.makeAccessTokenCookie(tokenInfo.getAccess_token()));
        response.addHeader("Set-Cookie", authService.makeRefreshTokenCookie(tokenInfo.getRefresh_token()));

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ObjectMapper objectMapper = new ObjectMapper();

        var responseMap = new HashMap<String, Object>();
        responseMap.put("redirect_url", redirectUrl);

        String responseJson = objectMapper.writeValueAsString(responseMap);
        response.getWriter().write(responseJson);
    }

}

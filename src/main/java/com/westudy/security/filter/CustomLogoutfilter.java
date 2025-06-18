package com.westudy.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.security.mapper.TokenMapper;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.AuthService;
import com.westudy.user.adapter.UserQueryAdapter;
import com.westudy.user.port.UserQueryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

public class CustomLogoutfilter extends OncePerRequestFilter {

    private final UserQueryPort userQueryPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenMapper tokenMapper;
    private final AuthService authService;

    public CustomLogoutfilter(UserQueryPort userQueryPort, JwtTokenProvider jwtTokenProvider, TokenMapper tokenMapper, AuthService authService) {
        this.userQueryPort = userQueryPort;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenMapper = tokenMapper;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (!request.getRequestURI().equals("/users/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        if(token != null){
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            long userid = userQueryPort.getUserIdByEmail(authentication.getName());

            tokenMapper.deleteByUserid(userid);

            Cookie cookie = authService.makeEmptyCookie();
            response.addCookie(cookie);

            ObjectMapper objectMapper = new ObjectMapper();

            var responseMap = new HashMap<String, Object>();
            responseMap.put("redirect_url", "/");

            String responseJson = objectMapper.writeValueAsString(responseMap);
            response.getWriter().write(responseJson);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

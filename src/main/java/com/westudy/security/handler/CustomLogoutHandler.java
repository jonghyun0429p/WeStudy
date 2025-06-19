package com.westudy.security.handler;

import com.westudy.security.mapper.TokenMapper;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.AuthService;
import com.westudy.user.port.UserQueryPort;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler  implements LogoutHandler {

    private final UserQueryPort userQueryPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenMapper tokenMapper;
    private final AuthService authService;

    public CustomLogoutHandler(UserQueryPort userQueryPort, JwtTokenProvider jwtTokenProvider, TokenMapper tokenMapper, AuthService authService) {
        this.userQueryPort = userQueryPort;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenMapper = tokenMapper;
        this.authService = authService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtTokenProvider.resolveToken(request);

        if(token != null){
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            long userId = userQueryPort.getUserIdByEmail(auth.getName());
            tokenMapper.deleteByUserid(userId);
        }

        Cookie cookie = authService.makeEmptyCookie();
        response.addCookie(cookie);
    }
}

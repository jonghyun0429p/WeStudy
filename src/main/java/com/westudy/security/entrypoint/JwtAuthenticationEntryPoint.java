package com.westudy.security.entrypoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        log.info("EntryPoint triggered - URI: {}, Accept: {}", uri, accept);

        boolean isApiRequest = uri.startsWith("/api");

        if (isApiRequest) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"로그인이 필요합니다.\"}");
        } else {
            response.sendRedirect("/page/user/login");
        }
    }
}




package com.westudy.security.filter;

import com.westudy.global.exception.BaseException;
import com.westudy.security.entrypoint.JwtAuthenticationEntryPoint;
import com.westudy.security.exception.JwtAuthenticationException;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final List<String> NO_AUTH_START = List.of(
            "/css", "/js", "/images", "/favicon.ico", "/swagger-ui", "/v3/api-docs", "/.well-known"
    );

    private boolean isSkip(String uri) {
        return (NO_AUTH_START.stream().anyMatch(uri::startsWith));
    }

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, AuthService authService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //URI 추출
        String requestURI = request.getRequestURI();
        logger.info("요청 URI: {}", requestURI);

        if (!isSkip(requestURI)) {
            try {
                authService.authenticateToken(request, response);
                logger.info("토큰 인증 성공");
            } catch (BaseException e) {
                SecurityContextHolder.clearContext();
                throw new JwtAuthenticationException(e);  // AuthenticationException 상속
            }
        }
        logger.info("토큰 통과 성공");
        filterChain.doFilter(request,response);

//        try {
//            authService.authenticateToken(request, response);
//            filterChain.doFilter(request,response);
//        }catch (TokenException error){
//            logger.info("토큰 인증 실패 " + error.getErrorCode());
//        }
    }
}

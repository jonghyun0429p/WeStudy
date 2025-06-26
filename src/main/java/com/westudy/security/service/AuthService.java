package com.westudy.security.service;

import com.westudy.global.exception.BaseException;
import com.westudy.security.dto.TokenInfo;
import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.enums.TokenErrorCode;
import com.westudy.security.enums.TokenType;
import com.westudy.security.mapper.TokenMapper;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.user.entity.User;
import com.westudy.security.port.UserQueryPort;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final long THIRTY_MINUTES = 60 * 30;
    private final long ONE_DAY = 60 * 60 * 24;

    private final TokenMapper tokenMapper;
    private final UserQueryPort userQueryPort;
    private final JwtTokenProvider jwtTokenProvider;

    public Authentication getAuthentication(String token){
        Claims claims = jwtTokenProvider.parseClaims(token, TokenType.ACCESS);
        List<String> roles = rolesEmptyCheck(claims.get("roles", List.class));

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                                                                    .map(SimpleGrantedAuthority::new)
                                                                    .collect(Collectors.toList());

        String email = claims.getSubject();
        User user = userQueryPort.getUserByEmail(email);

        CustomUserDetail principal = new CustomUserDetail(user);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Map<String, Object> getCurrentUserNickname(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(TokenErrorCode.MISSING_TOKEN);
        }

        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        return Map.of(
                "nickname", userDetail.getUserNickname()
        );
    }

    public void authenticateToken(HttpServletRequest request, HttpServletResponse response){
        String accessToken = null;

        try{
            //엑세스 토큰 인증
            accessToken = jwtTokenProvider.resolveAccessToken(request);
            Authentication auth = getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return;
        }catch (BaseException accessError){
            log.info("엑세스 토큰 문제 "+accessError.getErrorCode());
            try{
                String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
                Claims refreshClaims = jwtTokenProvider.parseClaims(refreshToken, TokenType.REFRESH);
                long userId = Long.parseLong(refreshClaims.getSubject());

                isSameRefresh(refreshToken, userId);

                TokenInfo tokenInfo = makeTokenWithRefreshToken(refreshToken);
                response.addHeader("Set-Cookie", makeAccessTokenCookie(tokenInfo.getAccess_token()));
                response.addHeader("Set-Cookie", makeRefreshTokenCookie(tokenInfo.getRefresh_token()));

                insertToken(userId, tokenInfo.getRefresh_token());

                Authentication auth = getAuthentication(tokenInfo.getAccess_token());
                SecurityContextHolder.getContext().setAuthentication(auth);

                return;
            }catch (BaseException refreshError){
                log.info("리프레시 토큰 문제 "+refreshError.getErrorCode());
                SecurityContextHolder.clearContext();
            }
        }
    }

    public TokenInfo makeTokenWithRefreshToken(String refreshToken){
        Claims claims = jwtTokenProvider.parseClaims(refreshToken, TokenType.REFRESH);
        long userId = Long.parseLong(claims.getSubject());
        User user = userQueryPort.getUserByUserId(userId);
        CustomUserDetail customUserDetail = new CustomUserDetail(user);

        return jwtTokenProvider.generateToken(customUserDetail.getAuthorities(), customUserDetail.getUserEmail(), customUserDetail.getUserNickname(), customUserDetail.getUserId());
    }

    public void deleteToken(long userid){
        tokenMapper.deleteByUserId(userid);
    }

    public void insertToken(long userid, String refreshToken){
        if(tokenMapper.findByUserId(userid) != null){
            tokenMapper.deleteByUserId(userid);
        }
        tokenMapper.insertToken(userid, refreshToken);
    }

    public String makeAccessTokenCookie(String accessToken){
         ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                                                        .httpOnly(true)
                                                        .secure(true)
                                                        .path("/")
                                                        .sameSite("Lax")
                                                        .maxAge(THIRTY_MINUTES)
                                                        .build();

        return accessCookie.toString();
    }

    public String makeRefreshTokenCookie(String refreshToken){
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(ONE_DAY)
                .build();

        return refreshCookie.toString();
    }

    public String makeEmptyCookie(String type){
        ResponseCookie cookie = ResponseCookie.from(type, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        return cookie.toString();
    }

    public List<String> rolesEmptyCheck(List<String> roles){
        if(roles == null){
            throw new BaseException(TokenErrorCode.INVALID_TOKEN);
        }
        return roles;
    }

    public void isSameRefresh(String refresh, long userId){
        String savedRefresh = tokenMapper.findByUserId(userId).getToken();
        if(savedRefresh == null){
            throw new BaseException(TokenErrorCode.MISSING_TOKEN);
        }
        if (!refresh.equals(savedRefresh)){
            throw new BaseException(TokenErrorCode.NOT_SAME_REFRESHTOKEN);
        }
    }
}



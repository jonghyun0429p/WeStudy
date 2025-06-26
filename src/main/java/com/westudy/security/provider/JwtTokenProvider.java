package com.westudy.security.provider;


import com.westudy.global.exception.BaseException;
import com.westudy.security.enums.TokenErrorCode;
import com.westudy.security.enums.TokenType;
import com.westudy.security.dto.TokenInfo;
import com.westudy.user.enums.UserErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final long THIRTY_MINUTES = 1000* 60 * 30;
    private final long ONE_DAY = 1000 * 60 * 60 * 24;
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public TokenInfo generateToken(Collection<? extends GrantedAuthority> authorityInfo, String email, String nickname, long userId) {

        List<String> roles = authorityInfo.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        long now = System.currentTimeMillis();

        String accessToken = generateAccessToken(email, roles, nickname);
        String refreshToken = generateRefreshToken(userId);

        return TokenInfo.builder()
                .grant_type("Bearer")
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    public String generateAccessToken(String email, List<String> roles, String nickname){


        String role = roles.stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_ROLE_ERROR));

        long now = System.currentTimeMillis();

        Date accessTokenExpiresIn = new Date(now + THIRTY_MINUTES);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", List.of(role))
                .claim("nickname", nickname)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(long userId){
        long now = System.currentTimeMillis();

        Date refreshTokenExpiresIn = new Date(now + ONE_DAY);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new BaseException(TokenErrorCode.COOKIE_NOT_HAVE_TOKEN);
    }
    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new BaseException(TokenErrorCode.COOKIE_NOT_HAVE_TOKEN);
    }

    public String getEmail(String token) {
        Claims claims = parseClaims(token, TokenType.ACCESS);
        if (claims.get("roles") == null) {
            throw new BaseException(TokenErrorCode.INVALID_TOKEN);
        }
        return claims.getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseClaims(token, TokenType.ACCESS);
        return claims.get("roles", List.class);
    }

    public Claims parseClaims(String token, TokenType type) {

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BaseException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new BaseException(TokenErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new BaseException(TokenErrorCode.MALFORMED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new BaseException(TokenErrorCode.MISSING_TOKEN);
        }

        if (type == TokenType.ACCESS) {
            if (claims.get("roles") == null || claims.get("nickname") == null) {
                throw new BaseException(TokenErrorCode.INVALID_TOKEN);
            }
        }
        return claims;
    }
}

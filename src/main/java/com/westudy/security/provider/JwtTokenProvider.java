package com.westudy.security.provider;


import com.westudy.security.exception.TokenErrorCode;
import com.westudy.security.exception.TokenException;
import com.westudy.security.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
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

    public TokenInfo generateToken(Collection<? extends GrantedAuthority> authorityInfo, String id) {

        List<String> roles = authorityInfo.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        long now = System.currentTimeMillis();

        Date accessTokenExpiresIn = new Date(now + THIRTY_MINUTES);
        String accessToken = Jwts.builder()
                .setSubject(id)
                .claim("roles", roles)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + ONE_DAY);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grant_type("Bearer")
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        List<String> roles = claims.get("roles", List.class);

        if(claims.get("auth") == null){
            throw new TokenException(TokenErrorCode.INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(TokenErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new TokenException(TokenErrorCode.MALFORMED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new TokenException(TokenErrorCode.MISSING_TOKEN);
        }
    }
}

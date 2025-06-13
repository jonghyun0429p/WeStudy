package com.westudy.security.service;

import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.exception.TokenErrorCode;
import com.westudy.security.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    public Map<String, Object> getCurrentUserNickname(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new TokenException(TokenErrorCode.MISSING_TOKEN);
        }

        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        return Map.of(
                "nickname", userDetail.getUserNickname()
        );
    }
}



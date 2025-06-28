package com.westudy.security.util;

import com.westudy.global.exception.BaseException;
import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.enums.SecurityErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static Long getCurrentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() instanceof CustomUserDetail userDetail){
            return userDetail.getUserId();
        }
        throw new BaseException(SecurityErrorCode.AUTHENTICATION_EMPTY);
    }

    public static String getCurrentNickname(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() instanceof CustomUserDetail userDetail){
            return userDetail.getUserNickname();
        }
        throw new BaseException(SecurityErrorCode.AUTHENTICATION_EMPTY);
    }

    public static String resolveCurrentNicknameSafely() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
                return userDetail.getUserNickname();
            }
        } catch (Exception ignored) {
            // 예외 무시
        }
        return null;
    }

    public static Long resolveCurrentUserIdSafely() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
                return userDetail.getUserId();
            }
        } catch (Exception ignored) {
            // 예외 무시
        }
        return null;
    }
}

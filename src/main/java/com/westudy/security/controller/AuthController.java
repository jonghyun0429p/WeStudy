package com.westudy.security.controller;

import com.westudy.security.entity.CustomUserDetail;
import com.westudy.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("check")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetail user) {
        if (user == null) {
            return ResponseEntity.ok(Map.of("isLogin", false));
        }
        return ResponseEntity.ok(Map.of("isLogin", true, "nickname", user.getUserNickname()));
    }
}

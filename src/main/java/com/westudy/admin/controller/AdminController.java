package com.westudy.admin.controller;

import com.westudy.admin.sevice.AdminService;
import com.westudy.security.entity.CustomUserDetail;
import com.westudy.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page/admin")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> adminPage(@AuthenticationPrincipal CustomUserDetail customUserDetail){

        String userEmail = customUserDetail.getUsername();

        return ResponseEntity.ok(adminService.getAdminPageData(userEmail));
    }
}

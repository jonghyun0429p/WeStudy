package com.westudy.admin.controller;

import com.westudy.security.entity.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin")
    public String getAdminPage(Model model, @AuthenticationPrincipal CustomUserDetail customUserDetail){
        String nickname = customUserDetail.getUserNickname();
        model.addAttribute("nickname", nickname);
        return "layout/admin/admin";
    }
}

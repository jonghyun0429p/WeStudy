package com.westudy.admin.controller;

import com.westudy.user.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/assign")
    public String adminPage(@AuthenticationPrincipal User user, Model model){

        model.addAttribute("email", user.getEmail());
        model.addAttribute("role", user.getRole());

        return "admin/admin";

    }
}

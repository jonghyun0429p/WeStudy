package com.westudy.user.controller;

import com.westudy.global.enums.PageErrorCode;
import com.westudy.global.exception.BaseException;
import com.westudy.security.util.SecurityUtil;
import com.westudy.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
@Controller
@RequestMapping("/page/user")
@Tag(name = "유저 페이지 컨트롤러", description = "유저 페이지 관련 렌더링 컨트롤러")
public class UserPageController {

    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    @Operation(summary = "유저 로그인 페이지", description = "유저 로그인 페이지 렌더링")
    public String getLoginPage(){
        return "layout/login";
    }

    @GetMapping("/signup")
    @Operation(summary = "유저 회원가입 페이지", description = "유저 회원가입 페이지 렌더링")
    public String getSignupPage(){ return "layout/user/usersignup"; }

    @GetMapping("/info/{mode}")
    @Operation(summary = "유저 정보, 수정 페이지", description = "유저 정보, 수정 페이지 렌더링")
    public String getUserpage(@PathVariable String mode, Model model){

        switch (mode) {
            case "edit":
                log.info("에딧 페이지 진입");
                model.addAttribute("user", userService.getUserEdit(SecurityUtil.getCurrentUserId()));
                return "/layout/user/useredit";
            case "data":
                log.info("정보 페이지 진입");
                model.addAttribute("user", userService.getUserInfo(SecurityUtil.getCurrentUserId()));
                return "/layout/user/userpage";
            default:
                throw new BaseException(PageErrorCode.PAGE_MISSING);
        }
    }
}

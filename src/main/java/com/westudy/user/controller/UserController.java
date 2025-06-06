package com.westudy.user.controller;

import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserLoginDTO;
import com.westudy.user.entity.User;
import com.westudy.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "유저 회원 가입", description = "유저 데이터를 입력받고 회원 가입 합니다.")
    public String signup(@RequestBody UserDTO userDto) {
        userService.register(userDto);
        return "redirect:/";
    }


}

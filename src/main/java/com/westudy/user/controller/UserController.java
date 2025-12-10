package com.westudy.user.controller;

import com.westudy.global.util.ResponseUtils;
import com.westudy.security.util.SecurityUtil;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserEditDTO;
import com.westudy.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "유저 회원가입", description = "유저를 회원가입 시킵니다.")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserDTO userDto) {
        userService.register(userDto);
        return ResponseUtils.redirect("/login");
    }

    @PostMapping("/update")
    @Operation(summary = "유저 정보 변경", description = "유저 변경 사항을 적용합니다.")
    public ResponseEntity<Map<String, String>> userPasswordUpdate(@RequestBody UserEditDTO userEditDTO){
        userService.updateUser(SecurityUtil.getCurrentUserId(), userEditDTO);
        return ResponseUtils.redirect("/");
    }
}

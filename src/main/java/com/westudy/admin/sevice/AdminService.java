package com.westudy.admin.sevice;

import com.westudy.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;

    public Map<String, Object> getAdminPageData(String userEmail){
        String nickname = userService.getUserNickname(userEmail);

        return Map.of(
                "nickname", nickname
        );

    }

}

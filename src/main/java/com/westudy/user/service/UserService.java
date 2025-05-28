package com.westudy.user.service;

import com.westudy.user.entity.User;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public void register(User user) {
        userMapper.insertUser(user);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}

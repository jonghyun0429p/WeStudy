package com.westudy.user.repository;

import com.westudy.user.entity.User;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UserMapper userMapper;

    public User findByUsername(String username){
        return userMapper.findByUsername(username);
    }

    public void insertUser(User user){
        userMapper.insertUser(user);
    }

    public void deleteByUserName(String username) {
        userMapper.deleteByUserName(username);
    }
}

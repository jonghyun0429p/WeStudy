package com.westudy.user.mapper;

import com.westudy.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUserId(Long userId);
    User findByUsername(String username);
    User findByEmail(String email);
    void insertUser(User user);
    void deleteByUserName(String username);
}

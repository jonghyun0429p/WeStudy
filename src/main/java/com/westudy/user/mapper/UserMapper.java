package com.westudy.user.mapper;

import com.westudy.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    void insertUser(User user);
}

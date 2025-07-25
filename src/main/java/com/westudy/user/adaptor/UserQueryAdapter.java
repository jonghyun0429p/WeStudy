package com.westudy.user.adapter;

import com.westudy.global.exception.BaseException;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserErrorCode;
import com.westudy.user.mapper.UserMapper;
import com.westudy.security.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {

    private final UserMapper userMapper;

    private User requireUser(User user){
        if(user == null){
            throw new BaseException(UserErrorCode.USER_EMPTY);
        }
        return user;
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return requireUser(userMapper.findByEmail(email)).getId();
    }

    @Override
    public User getUserByEmail(String email) {
        return requireUser(userMapper.findByEmail(email));
    }

    @Override
    public User getUserByUserId(long userId){
        return requireUser(userMapper.findByUserId(userId));
    }
}

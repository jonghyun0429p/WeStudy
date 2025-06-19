package com.westudy.user.adapter;

import com.westudy.user.entity.User;
import com.westudy.user.exception.UserErrorCode;
import com.westudy.user.exception.UserException;
import com.westudy.user.mapper.UserMapper;
import com.westudy.user.port.UserQueryPort;
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
            throw new UserException(UserErrorCode.USER_EMPTY);
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

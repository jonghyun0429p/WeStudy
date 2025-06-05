package com.westudy.user.service;

import com.westudy.user.converter.UserConverter;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.entity.User;
import com.westudy.user.exception.UserErrorCode;
import com.westudy.user.exception.UserException;
import com.westudy.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public void register(UserDTO userDto) {
        log.info("register user: {}", userDto.getUserName());
        checkDuplicateEmail(userDto.getEmail());
        User user = userConverter.toEntity(userDto);
        userMapper.insertUser(user);
        log.info("로그인 처리 완료");
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void checkDuplicateEmail(String email){
        log.info("checkDuplicateEmail");
        User user = userMapper.findByEmail(email);
        if(user != null){
            throw new UserException(UserErrorCode.USER_USERNAME_DUPLICATE);
        }
        log.info("중복 체크 완료.");
    }
}

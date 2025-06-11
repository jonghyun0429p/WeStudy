package com.westudy.security.service;


import com.westudy.security.entity.CustomUserDetail;
import com.westudy.user.entity.User;
import com.westudy.user.exception.UserErrorCode;
import com.westudy.user.exception.UserException;
import com.westudy.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByEmail 실행");
        User user = userMapper.findByEmail(email);
        log.info("User 추출");
        if(user == null) {
            log.info("사용자 추출 실패");
            throw new UserException(UserErrorCode.USER_USERNAME_UNEXITED);
        }
        return new CustomUserDetail(user);
    }
}

package com.westudy.user.service;

import com.westudy.global.exception.BaseException;
import com.westudy.user.converter.UserConverter;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserEditDTO;
import com.westudy.user.dto.UserInfoDTO;
import com.westudy.user.entity.User;
import com.westudy.user.exception.UserErrorCode;
import com.westudy.user.mapper.UserMapper;
import com.westudy.security.port.UserQueryPort;
import com.westudy.user.port.SecurityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserConverter userConverter;
    private final SecurityPort securityPort;

    public void register(UserDTO userDto) {
        log.info("register user: {}", userDto.getUsername());
        checkDuplicateEmail(userDto.getEmail());

        userDto.setPassword(securityPort.encodePassword(userDto.getPassword()));

        User user = userConverter.toEntity(userDto);
        userMapper.insertUser(user);
        log.info("로그인 처리 완료");
    }

    public void checkDuplicateEmail(String email){
        log.info("checkDuplicateEmail");
        User user = userMapper.findByEmail(email);
        if(user != null){
            throw new BaseException(UserErrorCode.USER_USERNAME_DUPLICATE);
        }
        log.info("중복 체크 완료.");
    }

    public void updateUser(long userId, UserEditDTO userEdit){
        User existedUser = findByUserId(userId);

        if(userEdit.getPassword() != ""){
            userEdit.setPassword(existedUser.getPassword());
        }else {
            userEdit.setUsername(securityPort.encodePassword(userEdit.getPassword()));
        }

        User updateUser = User.builder()
                .id(existedUser.getId())
                .username(userEdit.getUsername())
                .password(existedUser.getPassword().equals(userEdit.getPassword()) ? existedUser.getPassword() : userEdit.getPassword())
                .email(userEdit.getEmail())
                .nickname(userEdit.getNickname())
                .phoneNumber(userEdit.getPhoneNumber())
                .createdAt(existedUser.getCreatedAt())
                .role(existedUser.getRole())
                .build();

        userMapper.updateUser(updateUser);
    }

    public String getUserNickname(String userEmail){
        User user = userMapper.findByEmail(userEmail);
        return user.getNickname();
    }

    private User requireUser(User user){
        if(user == null){
            throw new BaseException(UserErrorCode.USER_EMPTY);
        }
        return user;
    }

    public UserInfoDTO getUserInfo(Long userId) {
        User user = findByUserId(userId);
        return UserInfoDTO.from(user);
    }

    public UserEditDTO getUserEdit(Long userId){
        User user = findByUserId(userId);
        return UserEditDTO.from(user);
    }

    public User findByUsername(String username) {
        return requireUser(userMapper.findByUsername(username));
    }

    public User findByUserId(long userId){ return requireUser(userMapper.findByUserId(userId)); }

    public  User findByEmail(String email){ return  requireUser(userMapper.findByEmail(email)); }

    public User getUserByUserId(long userId){
        return requireUser(userMapper.findByUserId(userId));
    }
}

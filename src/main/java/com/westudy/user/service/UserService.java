package com.westudy.user.service;

import com.westudy.global.exception.BaseException;
import com.westudy.global.util.RequireHelper;
import com.westudy.user.converter.UserConverter;
import com.westudy.user.dto.UserDTO;
import com.westudy.user.dto.UserEditDTO;
import com.westudy.user.dto.UserInfoDTO;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserErrorCode;
import com.westudy.user.mapper.UserMapper;
import com.westudy.user.port.SecurityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public UserInfoDTO getUserInfo(Long userId) {
        User user = findByUserId(userId);
        return userConverter.toUserInfo(user);
    }

    public UserEditDTO getUserEdit(Long userId){
        User user = findByUserId(userId);
        return userConverter.toUserEdit(user);
    }

    public User findByUsername(String username) {
        return RequireHelper.requireNonNull(
                userMapper.findByUsername(username), new BaseException(UserErrorCode.USER_EMPTY));
    }

    public User findByUserId(long userId){
        return RequireHelper.requireNonNull(
                userMapper.findByUserId(userId), new BaseException(UserErrorCode.USER_EMPTY)); }

    public  User findByEmail(String email){
        return RequireHelper.requireNonNull(
                userMapper.findByEmail(email), new BaseException(UserErrorCode.USER_EMPTY)); }

    public User getUserByUserId(long userId){

        return RequireHelper.requireNonNull(
                userMapper.findByUserId(userId), new BaseException(UserErrorCode.USER_EMPTY));
    }
}

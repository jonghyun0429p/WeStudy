package com.westudy.security.port;

import com.westudy.user.entity.User;

public interface UserQueryPort {
    Long getUserIdByEmail(String email);
    User getUserByEmail(String email);
    User getUserByUserId(long userId);
}

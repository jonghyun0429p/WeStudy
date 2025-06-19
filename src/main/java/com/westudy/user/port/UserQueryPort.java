package com.westudy.user.port;

import com.westudy.user.entity.User;

public interface UserQueryPort {
    Long getUserIdByEmail(String email);
    User getUserByEmail(String email);
    User getUserByUserId(long userId);
}

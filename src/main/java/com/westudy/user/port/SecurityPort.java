package com.westudy.user.port;

public interface SecurityPort {
    String encodePassword(String rawPassword);
    boolean matchesPassword(String rawPassword, String encodedPassword);
}

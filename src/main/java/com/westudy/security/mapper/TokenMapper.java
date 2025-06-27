package com.westudy.security.mapper;

import com.westudy.security.dto.TokenDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void insertToken(long userId, String token);

    void deleteByUserId(long userId);

    TokenDTO findByUserId(long userId);
}

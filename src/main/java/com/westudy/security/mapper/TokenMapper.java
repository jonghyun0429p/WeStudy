package com.westudy.security.mapper;

import com.westudy.security.dto.TokenDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
    void insertToken(long userid, String token);

    void deleteByUserid(long userid);

    TokenDTO findByUserid(long userid);
}

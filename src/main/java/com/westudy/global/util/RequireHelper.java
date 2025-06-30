package com.westudy.global.util;

import com.westudy.global.exception.BaseException;
import com.westudy.post.dto.PostDetailDBDTO;
import com.westudy.post.dto.PostResponseDTO;
import com.westudy.post.entity.Post;
import com.westudy.post.enums.PostErrorCode;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserErrorCode;

import java.util.List;

public class RequireHelper {

    public static <T> T requireNonNull(T value, BaseException e) {
        if (value == null) {
            throw e;
        }
        return value;
    }

    public static <T> List<T> requireNonEmpty(List<T> list, BaseException e){
        if(list == null || list.isEmpty()){
            throw e;
        }
        return list;
    }
}


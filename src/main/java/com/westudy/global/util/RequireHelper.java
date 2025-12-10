package com.westudy.global.util;

import com.westudy.global.exception.BaseException;

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


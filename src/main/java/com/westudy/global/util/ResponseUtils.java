package com.westudy.global.util;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    /**
     * redirect_url 키로 응답을 구성하여 반환
     *
     * @param url 클라이언트가 리디렉션할 URL
     * @return ResponseEntity<Map<String, String>> 형태의 응답
     */
    public static ResponseEntity<Map<String, String>> redirect(String url) {
        Map<String, String> result = new HashMap<>();
        result.put("redirect_url", url);
        return ResponseEntity.ok(result);
    }
}
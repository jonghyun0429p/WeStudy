package com.westudy.location.controller;

import com.westudy.location.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationApiController {

    private final KakaoMapService kakaoMapService;

    @GetMapping("/search")
    public ResponseEntity<String> searchPlaces(@RequestParam String keyword) {
        // 백엔드에서 카카오 API를 직접 호출하여 가져옴
        String result = kakaoMapService.searchPlaces(keyword);
        return ResponseEntity.ok(result);
    }
}

package com.westudy.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    private final RestTemplate restTemplate = new RestTemplate(); // 필요 시 Bean으로 등록하여 주입받아도 됨

    /**
     * 카카오 로컬 API: 키워드로 장소 검색
     * @param keyword 검색어 (예: "강남역 스터디룸")
     * @return 카카오 API 응답 JSON 문자열
     */
    public String searchPlaces(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "{\"documents\":[], \"message\":\"Keyword is empty\"}";
        }

        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + restApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to search places from Kakao API: {}", e.getMessage(), e);
            throw new RuntimeException("지도 검색 중 오류가 발생했습니다.");
        }
    }
}

package com.westudy.ai.client;

import com.westudy.ai.dto.AiQueryRequestDTO;
import com.westudy.ai.dto.AiQueryResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AiClient {

    private final RestTemplate restTemplate;
    private final String aiServerUrl;

    public AiClient(@Value("${ai-server.url}") String aiServerUrl) {
        this.restTemplate = new RestTemplate();
        this.aiServerUrl = aiServerUrl;
    }

    public void indexLog(Long studyId, Long logId, String title, String content) {
        String url = aiServerUrl + "/api/ai/index";
        log.info("FastAPI 일지 색인 요청 - url: {}, logId: {}", url, logId);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("studyId", studyId);
        requestMap.put("logId", logId);
        requestMap.put("title", title);
        requestMap.put("content", content);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);

            restTemplate.postForEntity(url, entity, String.class);
            log.info("FastAPI 일지 색인 성공 - logId: {}", logId);
        } catch (Exception e) {
            log.error("FastAPI 일지 색인 실패 - logId: {}, error: {}", logId, e.getMessage());
        }
    }

    public void deleteLog(Long logId) {
        String url = aiServerUrl + "/api/ai/index/" + logId;
        log.info("FastAPI 일지 삭제 요청 - url: {}, logId: {}", url, logId);

        try {
            restTemplate.delete(url);
            log.info("FastAPI 일지 삭제 성공 - logId: {}", logId);
        } catch (Exception e) {
            log.error("FastAPI 일지 삭제 실패 - logId: {}, error: {}", logId, e.getMessage());
        }
    }

    public AiQueryResponseDTO query(Long studyId, String queryText) {
        String url = aiServerUrl + "/api/ai/query";
        log.info("FastAPI RAG 질의 요청 - url: {}, studyId: {}, query: {}", url, studyId, queryText);

        AiQueryRequestDTO requestDTO = AiQueryRequestDTO.builder()
                .studyId(studyId)
                .queryText(queryText)
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AiQueryRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

            ResponseEntity<AiQueryResponseDTO> response = restTemplate.postForEntity(url, entity, AiQueryResponseDTO.class);
            log.info("FastAPI RAG 질의 성공 - studyId: {}", studyId);
            return response.getBody();
        } catch (Exception e) {
            log.error("FastAPI RAG 질의 실패 - studyId: {}, error: {}", studyId, e.getMessage());
            // 에러 발생 시 fallback용 스켈레톤 응답 반환
            return AiQueryResponseDTO.builder()
                    .answer("[Fallback] AI 서버가 응답하지 않습니다. 파이썬 서버 기동 상태를 확인해 주세요. 🔌")
                    .build();
        }
    }
}

package com.westudy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westudy.ai.client.AiClient;
import com.westudy.ai.dto.AiQueryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
public class AiClientTest {

    @Autowired
    private AiClient aiClient;

    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // AiClient 내부의 RestTemplate을 추출하여 MockRestServiceServer 세팅
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(aiClient, "restTemplate");
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("AiClient가 FastAPI 서버로 RAG 질의 요청을 올바르게 송신하고 역직렬화하는지 테스트")
    void testQuerySuccess() throws Exception {
        AiQueryResponseDTO mockResponse = AiQueryResponseDTO.builder()
                .answer("가상 테스트 AI 응답입니다.")
                .references(List.of(
                        AiQueryResponseDTO.ReferenceDTO.builder().logId(1L).title("테스트 일지").build()
                ))
                .build();

        mockServer.expect(requestTo("http://localhost:8000/api/ai/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studyId").value(10))
                .andExpect(jsonPath("$.queryText").value("CORS"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        AiQueryResponseDTO response = aiClient.query(10L, "CORS");

        mockServer.verify();
        assertNotNull(response);
        assertEquals("가상 테스트 AI 응답입니다.", response.getAnswer());
        assertEquals(1, response.getReferences().size());
        assertEquals("테스트 일지", response.getReferences().get(0).getTitle());
    }

    @Test
    @DisplayName("FastAPI 서버가 꺼져 있을 때 AiClient가 에러를 로깅하고 Fallback 응답을 반환하는지 테스트")
    void testQueryFallbackOnError() {
        mockServer.expect(requestTo("http://localhost:8000/api/ai/query"))
                .andRespond(withServerError());

        AiQueryResponseDTO response = aiClient.query(10L, "CORS");

        assertNotNull(response);
        assertTrue(response.getAnswer().contains("Fallback"));
    }
}

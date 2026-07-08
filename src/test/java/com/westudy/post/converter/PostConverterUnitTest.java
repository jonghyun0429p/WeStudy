package com.westudy.post.converter;

import com.westudy.post.dto.PostDetailDBDTO;
import com.westudy.post.dto.PostDetailResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PostConverterUnitTest {

    private final PostConverter postConverter = new PostConverter();

    @Test
    @DisplayName("작성자 본인 확인 검증 - 현재 사용자와 작성자 ID가 일치할 때")
    void testIsWriterTrue() {
        PostDetailDBDTO dbDto = new PostDetailDBDTO();
        dbDto.setPostId(1L);
        dbDto.setAuthorId(10L); // 작성자 ID 10
        dbDto.setNickname("작성자");
        dbDto.setCreatedAt(LocalDateTime.now());
        dbDto.setModifiedAt(LocalDateTime.now());

        PostDetailResponseDTO response = postConverter.toDetailResponseDTO(dbDto, 10L); // 현재 로그인한 사용자 ID 10

        assertTrue(response.isWriter(), "사용자 ID와 작성자 ID가 일치하면 isWriter는 true여야 합니다.");
    }

    @Test
    @DisplayName("작성자 본인 확인 검증 - 현재 사용자와 작성자 ID가 일치하지 않을 때")
    void testIsWriterFalse() {
        PostDetailDBDTO dbDto = new PostDetailDBDTO();
        dbDto.setPostId(1L);
        dbDto.setAuthorId(10L); // 작성자 ID 10
        dbDto.setNickname("작성자");
        dbDto.setCreatedAt(LocalDateTime.now());
        dbDto.setModifiedAt(LocalDateTime.now());

        PostDetailResponseDTO response = postConverter.toDetailResponseDTO(dbDto, 20L); // 다른 사용자 ID 20

        assertFalse(response.isWriter(), "사용자 ID와 작성자 ID가 다르면 isWriter는 false여야 합니다.");
    }

    @Test
    @DisplayName("작성자 본인 확인 검증 - 로그인하지 않은 경우")
    void testIsWriterWithNullUser() {
        PostDetailDBDTO dbDto = new PostDetailDBDTO();
        dbDto.setPostId(1L);
        dbDto.setAuthorId(10L);
        dbDto.setNickname("작성자");
        dbDto.setCreatedAt(LocalDateTime.now());
        dbDto.setModifiedAt(LocalDateTime.now());

        PostDetailResponseDTO response = postConverter.toDetailResponseDTO(dbDto, null); // 로그인 정보 없음

        assertFalse(response.isWriter(), "로그인 정보가 없으면 isWriter는 false여야 합니다.");
    }
}

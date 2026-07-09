package com.westudy.comment.controller;

import com.westudy.comment.dto.CommentDeleteRequest;
import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentResponseDTO;
import com.westudy.comment.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentControllerUnitTest {

    @Test
    @DisplayName("댓글 작성 요청 성공 시 리디렉션 처리 검증")
    void testInsertComment() {
        CommentService commentService = mock(CommentService.class);
        CommentController controller = new CommentController(commentService);

        CommentInsertDTO insertDto = new CommentInsertDTO();
        insertDto.setPostId(1L);
        insertDto.setContent("댓글 작성 테스트");

        ResponseEntity<Map<String, String>> response = controller.insertComment(insertDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("/post/detail?id=1", response.getBody().get("redirect_url"));

        verify(commentService, times(1)).insertComment(insertDto);
    }

    @Test
    @DisplayName("댓글 삭제 요청 성공 시 DTO 파싱 및 리디렉션 처리 검증")
    void testDeleteComment() {
        CommentService commentService = mock(CommentService.class);
        CommentController controller = new CommentController(commentService);

        CommentDeleteRequest deleteDto = new CommentDeleteRequest(123L);

        ResponseEntity<Map<String, String>> response = controller.deleteComment(deleteDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("/post", response.getBody().get("redirect_url"));

        verify(commentService, times(1)).deleteComment(123L);
    }

    @Test
    @DisplayName("댓글 목록 조회 요청 검증")
    void testFindCommentByPostId() {
        CommentService commentService = mock(CommentService.class);
        CommentController controller = new CommentController(commentService);

        long postId = 1L;
        List<CommentResponseDTO> comments = new ArrayList<>();
        CommentResponseDTO comment = new CommentResponseDTO();
        comment.setCommentId(100L);
        comments.add(comment);

        when(commentService.getCommentsByPostId(postId)).thenReturn(comments);

        ResponseEntity<List<CommentResponseDTO>> response = controller.findCommentBypostId(postId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());

        verify(commentService, times(1)).getCommentsByPostId(postId);
    }
}

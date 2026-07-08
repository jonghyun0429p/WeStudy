package com.westudy.post.controller;

import com.westudy.comment.dto.CommentResponseDTO;
import com.westudy.comment.service.CommentService;
import com.westudy.post.dto.PostDetailResponseDTO;
import com.westudy.post.service.PostSevice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostPageControllerUnitTest {

    @Test
    @DisplayName("게시글 상세 페이지 호출 시 댓글 목록 모델 바인딩 검증")
    void testGetPostDetailBindsComments() {
        // 서비스 모킹
        PostSevice postSevice = mock(PostSevice.class);
        CommentService commentService = mock(CommentService.class);

        // 컨트롤러 인스턴스 생성
        PostPageController controller = new PostPageController(postSevice, commentService);

        // 가상 데이터 준비
        long postId = 1L;
        PostDetailResponseDTO postDetail = new PostDetailResponseDTO();
        postDetail.setPostId(postId);
        postDetail.setTitle("테스트 글");

        List<CommentResponseDTO> comments = new ArrayList<>();
        CommentResponseDTO comment = new CommentResponseDTO();
        comment.setCommentId(100L);
        comment.setContent("댓글 내용");
        comments.add(comment);

        // 스터빙 설정
        when(postSevice.getPostDetailResponse(postId)).thenReturn(postDetail);
        when(commentService.getCommentsByPostId(postId)).thenReturn(comments);

        // 테스트용 모델 객체 생성
        Model model = new ConcurrentModel();

        // 컨트롤러 상세 페이지 호출
        String viewName = controller.getPostDetail(postId, model);

        // 결과 검증
        assertEquals("/layout/post/detail", viewName);
        assertTrue(model.containsAttribute("page"));
        assertTrue(model.containsAttribute("comments"));
        assertEquals(postDetail, model.getAttribute("page"));
        assertEquals(comments, model.getAttribute("comments"));

        verify(postSevice, times(1)).getPostDetailResponse(postId);
        verify(commentService, times(1)).getCommentsByPostId(postId);
    }
}

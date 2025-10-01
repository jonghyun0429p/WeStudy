package com.westudy.like.controller;

import com.westudy.global.util.ResponseUtils;
import com.westudy.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/api/like")
@Tag(name="좋아요 컨트롤러", description = "좋아요 늘렀을 때 적용")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @RequestMapping("/post")
    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요 적용")
    public ResponseEntity<Map<String, String>> likePost(@RequestBody long postId){
        likeService.insertPostLike(postId);
        return ResponseUtils.ok("게시글 좋아요 성공");
    }

    @RequestMapping("/comment")
    @Operation(summary = "댓글 좋아요", description = "댓글에 좋아요 적용")
    public ResponseEntity<Map<String, String>> likeComment(@RequestBody long commentId){
        likeService.insertCommentLike(commentId);
        return ResponseUtils.ok("댓글 좋아요 성공");
    }

    @RequestMapping("/notLikePost")
    @Operation(summary = "게시글 좋아요 취소", description = "게시글에 좋아요 취소")
    public ResponseEntity<Map<String, String>> notLikePost(@RequestBody long postId){
        likeService.notlikePostLike(postId);
        return ResponseUtils.ok("게시글 좋아요 취소 성공");
    }

    @RequestMapping("/notLikeComment")
    @Operation(summary = "댓글 좋아요 취소", description = "댓글에 좋아요 취소")
    public ResponseEntity<Map<String, String>> notLikeComment(@RequestBody long postId){
        likeService.notlikeCommentLike(postId);
        return ResponseUtils.ok("댓글 좋아요 취소 성공");
    }

    //이제 포스트랑 댓글 데이터를 불러올 때 사용할 좋아요을 어떻게 불러올지 고민해보자.
}

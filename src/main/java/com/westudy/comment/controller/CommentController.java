package com.westudy.comment.controller;

import com.westudy.comment.dto.CommentDeleteRequest;
import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.comment.entity.Comment;
import com.westudy.comment.service.CommentService;
import com.westudy.global.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/comments")
@Tag(name = "댓글 컨트롤러", description = "댓글 요청시 데이터 제공")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    @Operation(summary = "댓글 작성", description = "댓글 저장하는 api")
    public ResponseEntity<Map<String, String>> insertComment(@RequestBody CommentInsertDTO commentInsertDTO){
        commentService.insertComment(commentInsertDTO);
        return ResponseUtils.redirect("/post/detail?id="+commentInsertDTO.getPostId());
    }

    @PostMapping("/post")
    @Operation(summary = "댓글 읽어오기", description = "게시글에 들어가면 js가 댓글요청을 하고, 그 요청에 데이터를 제공")
    public ResponseEntity<List<Comment>> findCommentBypostId(@RequestBody long postId){
        List<Comment> comments = commentService.findCommentById(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/update")
    @Operation(summary = "댓글 수정하기", description = "댓글을 수정하면 적용하는 api")
    public ResponseEntity<Map<String, String>> updateComment(@RequestBody CommentUpdateDTO commentUpdateDTO){
        commentService.updateComment(commentUpdateDTO);

        return ResponseUtils.redirect("/post/detail?id="+commentUpdateDTO.getPostId());
    }

    @PostMapping("/delete")
    @Operation(summary = "댓글 삭제하기", description = "댓글 소프트 삭제하는 api")
    public ResponseEntity<Map<String, String>> deleteComment(@RequestBody CommentDeleteRequest request){
        commentService.deleteComment(request.commentId());

        return ResponseUtils.redirect("/post");
    }
}

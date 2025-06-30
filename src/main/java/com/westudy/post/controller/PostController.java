package com.westudy.post.controller;

import com.westudy.global.util.ResponseUtils;
import com.westudy.post.dto.PostInsertDTO;
import com.westudy.post.dto.PostUpdateDTO;
import com.westudy.post.service.PostSevice;
import com.westudy.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@Tag(name = "Post Controller", description = "게시글 컨트롤러")
@RequestMapping("/api/post")
public class PostController {

    private final PostSevice postSevice;

    public PostController(PostSevice postSevice) {
        this.postSevice = postSevice;
    }


    @PostMapping("/insert")
    @Operation(summary = "게시글 작성", description = "게시글을 저장합니다.")
    public ResponseEntity<Map<String, String>> insertPost(@RequestBody PostInsertDTO postDTO){
        log.info("게시글 작성 요청 진입");
        postSevice.insertPost(SecurityUtil.getCurrentUserId(), postDTO);
        log.info("게시글 작성 성공");
        return ResponseUtils.redirect("/page/post");
    }

    @PostMapping("/update")
    @Operation(summary = "게시글 수정", description = "게시글 수정합니다.")
    public ResponseEntity<Map<String, String>> updatePost(@RequestBody PostUpdateDTO postUpdateDTO){
        postSevice.updatePost(postUpdateDTO);
        return ResponseUtils.redirect("/page/post/detail?id="+postUpdateDTO.getPostId());
    }

    @PostMapping("/delete")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제합니다.")
    public ResponseEntity<Map<String, String>> deletePost(@RequestBody long id){
        postSevice.deletePost(id);
        return ResponseUtils.redirect("/post/page");
    }
}

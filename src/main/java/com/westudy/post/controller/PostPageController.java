package com.westudy.post.controller;

import com.westudy.post.enums.PostCategory;
import com.westudy.post.service.PostSevice;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/page/post")
public class PostPageController {

    private final PostSevice postSevice;

    public PostPageController(PostSevice postSevice) {
        this.postSevice = postSevice;
    }

    @GetMapping({"/",""})
    @Operation(summary = "게시글 요청", description = "기본 게시글 요청 페이지 공지사항 2개 + 게시글 20개 띄우기")
    public String getPostPage(
            @RequestParam(value = "page", defaultValue = "1")int page,
            Model model){

        model.addAttribute("pages", postSevice.getPostList(page));
        model.addAttribute("pageCount", postSevice.getPostPage());
        model.addAttribute("currentPage", page);
        return "/layout/post/board";
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "게시글 검색시 탐색")
    public String getPostSearch(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model){

            model.addAttribute("pages", postSevice.findSearchPosts(keyword, page));
            model.addAttribute("pageCount", postSevice.getSearchedPostCount(keyword));
            model.addAttribute("currentPage", page);

            return "/layout/post/board";

    }

    @GetMapping("/write")
    @Operation(summary = "게시글 작성", description = "게시글 작성시 나오는 페이지")
    public String getPostWrite(){

        return "/layout/post/write";
    }

    @GetMapping("/detail")
    @Operation(summary = "게시글 세부 페이지", description = "게시글 클릭시 세부 내용")
    public String getPostDetail(
            @RequestParam(value = "id")Long id,
            Model model){


        model.addAttribute("page", postSevice.getPostDetailResponse(id));
        model.addAttribute("categories", PostCategory.values());
        return "/layout/post/detail";
    }

    //에딧 과정은 사용자가 작성자와 같은지 비교하고, 같다면 이제 수정하는 과정을 거친다.
    @GetMapping("/detail/edit")
    @Operation(summary = "게시글 수정 페이지", description = "게시글 수정 클릭시 수정페이지")
    public String getPostEdit(
            @RequestParam(value = "id")Long id,
            Model model){
        model.addAttribute("page", postSevice.getPostDetailResponse(id));
        model.addAttribute("categories", PostCategory.values());
        return "/layout/post/edit";
    }
}

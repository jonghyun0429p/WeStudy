package com.westudy.study.controller;

import com.westudy.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/page/study")
public class StudyPageController {

    private final StudyService studyService;

    public StudyPageController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("")
    @Operation(summary = "기본 스터디 페이지", description = "게시글과 동일하게 공지사항 2개 스터디 내용 20개, 로그인이 되어있다면 비교해서 수정가능한지 띄우기까지.")
    public String getStudyPage(
        @RequestParam(value = "page", defaultValue = "1")int page,
        Model model){

            model.addAttribute("pages", studyService.getStudyList(page));
            model.addAttribute("pageCount", studyService.getStudyCount());
            model.addAttribute("currentPage", page);

        return "/layout/study/board";
    }

    @GetMapping("/search")
    @Operation(summary = "검색한 스터디 페이지", description = "제목만 검사해서 확인")
    public String getSearchStudyPage(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model){

        model.addAttribute("pages", studyService.findSearchStudy(keyword, page));
        model.addAttribute("pageCount", studyService.getSearchedStudyCount(keyword));
        model.addAttribute("currentPage", page);
        return "/layout/study/board";
    }

    @GetMapping("/write")
    @Operation(summary = "스터디 작성 페이지", description = "스터디 작성 페이지")
    public String getWriteStudyPage(){
        return "/layout/study/write";
    }

    @GetMapping("/edit")
    @Operation(summary = "스터디 수정 페이지", description = "수정을 누르면 수정이 가능한 페이지로 이동")
    public String getEditStudyPage(
            @RequestParam(value = "id")Long id,
            Model model){

        model.addAttribute("page", studyService.getStudyDetailResponse(id));

        return "/layout/study/edit";
    }

    @GetMapping("/manage")
    @Operation(summary = "스터디 신청자 관리 페이지", description = "스터디 신청자들 관리하는 페이지")
    public String getStudyParticipantManage(
            @RequestParam(value = "id") long id,
            Model model) {
        model.addAttribute("participant", studyService.getParticipantList(id));

        return "/layout/study/detail";
    }
}

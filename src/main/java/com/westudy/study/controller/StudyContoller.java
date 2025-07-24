package com.westudy.study.controller;

import com.westudy.global.util.ResponseUtils;
import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/study")
public class StudyContoller {

    private final StudyService studyService;

    public StudyContoller(StudyService studyService) {
        this.studyService = studyService;
    }

    @PostMapping("/application")
    @Operation(summary = "스터디 신청", description = "스터디 신청 상태로 변환")
    public ResponseEntity<Map<String, String>> applicationStudy(@RequestBody long studyId){

        studyService.applicationStudy(studyId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/approve")
    @Operation(summary = "스터디 인원 승인", description = "스터디 신청 인원 승인 요청")
    public ResponseEntity<Map<String, String>> approveStudy(@RequestBody long studyId, long userId){
        boolean isFull = studyService.approveAndCheckIfFull(userId, studyId);
        log.info("스터디 인원 승인 성공");

        Map<String, String> response = new HashMap<>();
        response.put("status", isFull ? "FULL" : "OK");

        return ResponseEntity.ok(response); // 항상 200
    }

    @PostMapping("/reject")
    @Operation(summary = "스터디 인원 거절", description = "스터디 인원 거절 요청")
    public ResponseEntity<Map<String, String>> resultStudy(@RequestBody long studyId, long userId){
        studyService.requestReject(userId, studyId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/insert")
    @Operation(summary = "스터디 생성", description = "스터디를 생성하면 DB에 저장")
    public ResponseEntity<Map<String, String>> insertStudy(@RequestBody StudyInsertDTO studyInsertDTO){
        studyService.insertStudy(studyInsertDTO);
        log.info("스터디 생성 성공");

        return ResponseUtils.redirect("/page/study");
    }

    @PostMapping("/update")
    @Operation(summary = "스터디 내용 변경", description = "스터디 변경사항 적용")
    public ResponseEntity<Map<String, String>> updateStudy(@RequestBody StudyUpdateDTO studyUpdateDTO){
        studyService.updateStudy(studyUpdateDTO);
        log.info("스터디 업데이트 성공");

        return ResponseUtils.redirect("/page/study/detail?id="+studyUpdateDTO.getId());
    }

    @PostMapping("/delete")
    @Operation(summary = "제거 시간 지정", description = "soft delete를 적용하는 제거 과정")
    public ResponseEntity<Map<String, String>> deleteStudy(@RequestBody long id){
        studyService.deleteStudy(id);
        log.info("스터디 제거 설정");

        return ResponseUtils.redirect("/page/study");
    }
}

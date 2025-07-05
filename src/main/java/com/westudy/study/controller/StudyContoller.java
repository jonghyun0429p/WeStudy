package com.westudy.study.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/study")
public class StudyContoller {

    @PostMapping("/insert")
    @Operation(summary = "스터디 생성", description = "스터디를 생성하면 DB에 저장")
    public ResponseEntity<Map<String, String>> insertStudy(){

    }

    @PostMapping("/update")
    @Operation(summary = "스터디 내용 변경", description = "스터디 변경사항 적용")
    public ResponseEntity<Map<String, String>> updateStudy(){

    }

    @PostMapping("/delete")
    @Operation(summary = "제거 시간 지정", description = "soft delete를 적용하는 제거 과정")
    public ResponseEntity<Map<String, String>> deleteStudy(){

    }
}

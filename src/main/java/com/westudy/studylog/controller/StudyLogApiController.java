package com.westudy.studylog.controller;

import com.westudy.security.util.SecurityUtil;
import com.westudy.studylog.dto.StudyLogRequestDTO;
import com.westudy.studylog.dto.StudyLogResponseDTO;
import com.westudy.studylog.service.StudyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies/{studyId}/logs")
@RequiredArgsConstructor
@Tag(name = "Study Log API Controller", description = "스터디 공동 일지(위키) API")
public class StudyLogApiController {

    private final StudyLogService studyLogService;

    @GetMapping
    @Operation(summary = "스터디 일지 목록 조회")
    public ResponseEntity<List<StudyLogResponseDTO>> getLogs(@PathVariable("studyId") Long studyId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(studyLogService.getStudyLogs(studyId, userId));
    }

    @PostMapping
    @Operation(summary = "스터디 일지 등록")
    public ResponseEntity<Void> createLog(
            @PathVariable("studyId") Long studyId,
            @RequestBody StudyLogRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        studyLogService.createStudyLog(studyId, userId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{logId}")
    @Operation(summary = "스터디 일지 수정")
    public ResponseEntity<Void> updateLog(
            @PathVariable("studyId") Long studyId,
            @PathVariable("logId") Long logId,
            @RequestBody StudyLogRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        studyLogService.updateStudyLog(studyId, logId, userId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{logId}")
    @Operation(summary = "스터디 일지 삭제")
    public ResponseEntity<Void> deleteLog(
            @PathVariable("studyId") Long studyId,
            @PathVariable("logId") Long logId) {
        Long userId = SecurityUtil.getCurrentUserId();
        studyLogService.deleteStudyLog(studyId, logId, userId);
        return ResponseEntity.ok().build();
    }
}

package com.westudy.alarm.controller;

import com.westudy.alarm.dto.AlarmResponseDTO;
import com.westudy.alarm.service.AlarmService;
import com.westudy.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
@Tag(name = "Alarm Controller", description = "실시간 알람 API (SSE)")
public class AlarmController {

    private final AlarmService alarmService;

    /**
     * SSE 연결 구독
     * @return SseEmitter
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "실시간 알람 구독", description = "SSE 연결을 통해 실시간 알람을 수신합니다.")
    public SseEmitter subscribe() {
        Long userId = SecurityUtil.getCurrentUserId();
        log.info("SSE subscribe - userId: {}", userId);
        return alarmService.subscribe(userId);
    }

    /**
     * 이전 알람 목록 조회
     */
    @GetMapping
    @Operation(summary = "알람 목록 조회", description = "현재 유저의 최근 알람 목록을 조회합니다.")
    public ResponseEntity<List<AlarmResponseDTO>> getAlarms() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(alarmService.getAlarmList(userId));
    }

    /**
     * 안읽은 알람 개수 조회 (Polling 용도 등으로 활용 가능)
     */
    @GetMapping("/unread-count")
    @Operation(summary = "안읽은 알람 개수", description = "현재 유저의 안읽은 알람 개수를 조회합니다.")
    public ResponseEntity<Integer> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(alarmService.getUnreadCount(userId));
    }

    /**
     * 개별 알람 읽음 처리
     */
    @PostMapping("/{alarmId}/read")
    @Operation(summary = "개별 알람 읽음 처리")
    public ResponseEntity<Void> readAlarm(@PathVariable("alarmId") Long alarmId) {
        alarmService.markAsRead(alarmId);
        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알람 읽음 처리
     */
    @PostMapping("/read-all")
    @Operation(summary = "모든 알람 읽음 처리")
    public ResponseEntity<Void> readAllAlarms() {
        Long userId = SecurityUtil.getCurrentUserId();
        alarmService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}

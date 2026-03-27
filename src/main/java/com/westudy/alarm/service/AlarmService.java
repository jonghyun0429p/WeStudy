package com.westudy.alarm.service;

import com.westudy.alarm.dto.AlarmResponseDTO;
import com.westudy.alarm.entity.Alarm;
import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.mapper.AlarmMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;
    
    // 유저 ID별 SseEmitter 관리 (메모리 방식 - 레거시 병목 지점: 분산 환경 불리)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * SSE 연결 구독
     */
    public SseEmitter subscribe(Long userId) {
        // 1시간 타임아웃
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(userId, emitter);

        // 연결 종료/타임아웃 시 맵에서 제거
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        // 연결 시 더미 데이터 전송 (503 에러 방지)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * 알람 전송
     */
    public void send(Long receiverId, Long senderId, AlarmType type, String content, String targetUrl) {
        Alarm alarm = Alarm.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .type(type)
                .content(content)
                .targetUrl(targetUrl)
                .build();

        // 1. DB 저장 (Bottleneck 빌드업)
        alarmMapper.insertAlarm(alarm);

        // 2. 현재 로그인 중인 유저라면 실시간 전송
        if (emitters.containsKey(receiverId)) {
            SseEmitter emitter = emitters.get(receiverId);
            try {
                emitter.send(SseEmitter.event()
                        .name("alarm")
                        .data(AlarmResponseDTO.builder()
                                .id(alarm.getId())
                                .type(type)
                                .content(content)
                                .targetUrl(targetUrl)
                                .isRead(false)
                                .createdAt(alarm.getCreatedAt())
                                .build()));
            } catch (IOException e) {
                emitters.remove(receiverId);
                log.warn("알람 전송 실패 - userId: {}", receiverId);
            }
        }
    }

    public List<AlarmResponseDTO> getAlarmList(Long userId) {
        return alarmMapper.findByReceiverId(userId);
    }

    public int getUnreadCount(Long userId) {
        return alarmMapper.countUnreadByReceiverId(userId);
    }

    public void markAsRead(Long alarmId) {
        alarmMapper.markAsRead(alarmId);
    }

    public void markAllAsRead(Long userId) {
        alarmMapper.markAllAsRead(userId);
    }
}

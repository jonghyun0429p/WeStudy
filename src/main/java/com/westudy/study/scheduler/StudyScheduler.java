package com.westudy.study.scheduler;

import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.service.AlarmService;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.event.StudyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyScheduler {

    private final StudyMapper studyMapper;
    private final AlarmService alarmService;
    private final ApplicationEventPublisher eventPublisher;

    // 매일 자정(00:00:00)에 실행되는 스케줄러.
    // 모집 중이면서 마감 기한이 지난 스터디를 자동으로 마감(CLOSED) 처리합니다.
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoCloseExpiredStudies() {
        log.info("[Scheduler] 마감 기한이 지난 스터디 자동 마감 배치 시작");

        // 1. 마감 대상인 스터디 ID 목록 조회
        List<Long> expiredStudyIds = studyMapper.findExpiredStudyIds();
        
        if (expiredStudyIds.isEmpty()) {
            log.info("[Scheduler] 마감 대상 스터디가 없습니다.");
            return;
        }

        // 2. 스터디 상태를 CLOSED로 일괄 변경
        studyMapper.closeExpiredStudies();
        log.info("[Scheduler] {} 개의 스터디가 마감(CLOSED) 처리되었습니다.", expiredStudyIds.size());

        // 3. 마감된 스터디의 작성자와 승인된 참여자에게 알람 발송
        for (Long studyId : expiredStudyIds) {
            long leaderId = studyMapper.findUserIdByStudyId(studyId);
            List<Long> participantIds = studyMapper.findParticipantIds(studyId);

            String message = "모집 기간이 만료되어 스터디가 자동 마감되었습니다.";
            String targetUrl = "/page/study/detail?id=" + studyId;

            // 방장에게 알림
            alarmService.send(leaderId, leaderId, AlarmType.STUDY_REJECT, message, targetUrl);

            // 참여자들에게 알림
            for (Long participantId : participantIds) {
                alarmService.send(participantId, leaderId, AlarmType.STUDY_REJECT, message, targetUrl);
            }

            // Elasticsearch 동기화 이벤트 발행
            eventPublisher.publishEvent(new StudyEvent(studyId, "SAVE"));
        }
        
        log.info("[Scheduler] 스터디 자동 마감 배치 완료");
    }
}

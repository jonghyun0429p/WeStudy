package com.westudy.study.scheduler;

import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.service.AlarmService;
import com.westudy.study.mapper.StudyMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudySchedulerTest {

    @Mock
    private StudyMapper studyMapper;

    @Mock
    private AlarmService alarmService;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private StudyScheduler studyScheduler;

    @Test
    @DisplayName("만료된 스터디가 있으면 상태를 변경하고 관련 인원에게 알림을 발송함")
    void autoCloseExpiredStudies_Success() {
        // given
        List<Long> expiredStudyIds = Arrays.asList(1L);
        when(studyMapper.findExpiredStudyIds()).thenReturn(expiredStudyIds);
        when(studyMapper.findUserIdByStudyId(1L)).thenReturn(10L);
        when(studyMapper.findParticipantIds(1L)).thenReturn(Arrays.asList(11L));

        // when
        studyScheduler.autoCloseExpiredStudies();

        // then
        verify(studyMapper, times(1)).closeExpiredStudies();
        verify(alarmService, times(1)).send(eq(10L), eq(10L), eq(AlarmType.STUDY_REJECT), anyString(), anyString());
        verify(alarmService, times(1)).send(eq(11L), eq(10L), eq(AlarmType.STUDY_REJECT), anyString(), anyString());
    }
}

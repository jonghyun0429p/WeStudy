package com.westudy;

import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AlarmServiceTest {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("알림 발송 시 비동기 스레드 풀(alarm-async-)을 통해 독립적으로 실행되는지 검증")
    void testAsyncAlarmSending() throws Exception {
        // [1] 사전 데이터베이스 알림 카운트 조회
        Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alarm", Integer.class);

        try {
            // [2] 비동기로 알림 발송 호출
            alarmService.send(1L, 2L, AlarmType.STUDY_APPROVE, "비동기 테스트 알림", "/test-url");

            // [3] 비동기 처리 완료 대기 (최대 2초간 대기하며 DB에 적재되었는지 확인)
            boolean isInserted = false;
            for (int i = 0; i < 20; i++) {
                Thread.sleep(100);
                Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM alarm", Integer.class);
                if (afterCount > beforeCount) {
                    isInserted = true;
                    break;
                }
            }

            assertTrue(isInserted, "백그라운드 스레드에 의해 알림이 데이터베이스에 삽입되어야 합니다.");
        } finally {
            // [4] 데이터베이스 클린업
            jdbcTemplate.update("DELETE FROM alarm WHERE content = '비동기 테스트 알림'");
        }
    }
}

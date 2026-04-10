package com.westudy;

import com.westudy.security.entity.CustomUserDetail;
import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.enums.StudyStates;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import com.westudy.study.service.StudyService;
import com.westudy.user.entity.User;
import com.westudy.user.enums.UserRole;
import com.westudy.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
public class StudyConcurrencyTest {

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyMapper studyMapper;

    @Autowired
    private StudyParticipantMapper studyParticipantMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("비관적 잠금 적용 - 10명이 동시에 승인 요청 시 정확히 1명만 승인되어야 함")
    void concurrencyApprovalTest() throws InterruptedException {
        // given
        // 1. 방장 생성
        User host = createAndSaveUser("host", "host@test.com");
        
        // 2. 스터디 생성 (최대 인원 1명)
        Study study = Study.builder()
                .userId(host.getId())
                .title("동시성 테스트 스터디")
                .location("서울")
                .maxMember(1)
                .state(StudyStates.RECRUITING)
                .deadline(LocalDateTime.now().plusDays(7))
                .build();
        studyMapper.insertStudy(study);
        long studyId = study.getId();

        // 3. 대기자 10명 생성 및 신청 완료
        int waiterCount = 10;
        List<User> waiters = new ArrayList<>();
        for (int i = 1; i <= waiterCount; i++) {
            User waiter = createAndSaveUser("waiter" + i, "waiter" + i + "@test.com");
            waiters.add(waiter);
            
            StudyParticipant participant = StudyParticipant.builder()
                    .studyId(studyId)
                    .userId(waiter.getId())
                    .status(StudyParticipantStatus.WAITING)
                    .build();
            studyParticipantMapper.insertStudyParticipant(participant);
        }

        // 4. 동시성 테스트 준비
        ExecutorService executorService = Executors.newFixedThreadPool(waiterCount);
        CountDownLatch latch = new CountDownLatch(waiterCount);

        // when
        for (User waiter : waiters) {
            executorService.execute(() -> {
                try {
                    // 각 스레드마다 다른 사용자 정보를 SecurityContext에 주입
                    setAuthentication(waiter);
                    studyService.approveAndCheckIfFull(waiter.getId(), studyId);
                } catch (Exception e) {
                    System.out.println("승인 실패 (예상된 결과): " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        int approvedCount = studyParticipantMapper.countStudyMember(studyId);
        
        // 비관적 잠금이 정상 작동했다면, 아무리 동시에 요청해도 maxMember인 1명을 초과할 수 없음
        assertThat(approvedCount).isEqualTo(1);
        
        // 스터디 상태가 CLOSED로 변경되었는지 확인
        StudyResponseDTO studyResult = studyMapper.findByStudyId(studyId);
        assertThat(studyResult.getState()).isEqualTo(StudyStates.CLOSED);
    }

    private User createAndSaveUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("password")
                .nickname(username)
                .role(UserRole.ROLE_USER)
                .build();
        userMapper.insertUser(user);
        return userMapper.findByUsername(username);
    }

    private void setAuthentication(User user) {
        CustomUserDetail userDetail = new CustomUserDetail(user);
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

package com.westudy.studylog.service;

import com.westudy.global.exception.BaseException;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyErrorCode;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import com.westudy.studylog.dto.StudyLogRequestDTO;
import com.westudy.studylog.dto.StudyLogResponseDTO;
import com.westudy.studylog.entity.StudyLog;
import com.westudy.studylog.mapper.StudyLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyLogService {

    private final StudyLogMapper studyLogMapper;
    private final StudyParticipantMapper studyParticipantMapper;
    private final StudyMapper studyMapper;

    /**
     * 스터디 소속 멤버인지 검증 (방장 또는 승인된 회원만 가능)
     */
    public void validateMember(Long studyId, Long userId) {
        log.info("스터디 일지 권한 검증 - userId: {}, studyId: {}", userId, studyId);
        
        // 1. 방장인지 확인
        Long leaderId = studyMapper.findUserIdByStudyId(studyId);
        if (leaderId != null && leaderId.equals(userId)) {
            return;
        }

        // 2. 승인된 참여자인지 확인
        StudyParticipant participant = studyParticipantMapper.findParticipant(userId, studyId);
        if (participant == null || participant.getStatus() != StudyParticipantStatus.APPROVED) {
            log.warn("권한이 없는 사용자의 일지 접근 시도: userId = {}, status = {}", userId, participant != null ? participant.getStatus() : "null");
            throw new BaseException(StudyErrorCode.STUDY_MEMBER_UNAUTHORIZED);
        }
    }

    /**
     * 일지 목록 조회
     */
    public List<StudyLogResponseDTO> getStudyLogs(Long studyId, Long userId) {
        validateMember(studyId, userId);
        
        Long leaderId = studyMapper.findUserIdByStudyId(studyId);
        List<StudyLogResponseDTO> logs = studyLogMapper.findByStudyId(studyId);
        
        // 수정/삭제 가능 여부(isEditable) 동적 매핑
        for (StudyLogResponseDTO logDto : logs) {
            boolean isEditable = logDto.getUserId().equals(userId) || (leaderId != null && leaderId.equals(userId));
            logDto.setEditable(isEditable);
        }
        
        return logs;
    }

    /**
     * 일지 작성
     */
    @Transactional
    public void createStudyLog(Long studyId, Long userId, StudyLogRequestDTO requestDTO) {
        validateMember(studyId, userId);
        
        StudyLog logEntity = StudyLog.builder()
                .studyId(studyId)
                .userId(userId)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();
        
        studyLogMapper.insertLog(logEntity);
    }

    /**
     * 일지 수정
     */
    @Transactional
    public void updateStudyLog(Long studyId, Long logId, Long userId, StudyLogRequestDTO requestDTO) {
        validateMember(studyId, userId);
        
        StudyLog existingLog = studyLogMapper.findById(logId);
        if (existingLog == null) {
            throw new IllegalArgumentException("존재하지 않는 일지입니다.");
        }
        if (!existingLog.getStudyId().equals(studyId)) {
            throw new IllegalArgumentException("일치하지 않는 스터디 일지입니다.");
        }

        Long leaderId = studyMapper.findUserIdByStudyId(studyId);
        boolean isAuthor = existingLog.getUserId().equals(userId);
        boolean isHost = leaderId != null && leaderId.equals(userId);

        if (!isAuthor && !isHost) {
            throw new BaseException(StudyErrorCode.STUDY_MEMBER_UNAUTHORIZED);
        }

        StudyLog updatedLog = StudyLog.builder()
                .id(logId)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();

        studyLogMapper.updateLog(updatedLog);
    }

    /**
     * 일지 삭제
     */
    @Transactional
    public void deleteStudyLog(Long studyId, Long logId, Long userId) {
        validateMember(studyId, userId);

        StudyLog existingLog = studyLogMapper.findById(logId);
        if (existingLog == null) {
            throw new IllegalArgumentException("존재하지 않는 일지입니다.");
        }
        if (!existingLog.getStudyId().equals(studyId)) {
            throw new IllegalArgumentException("일치하지 않는 스터디 일지입니다.");
        }

        Long leaderId = studyMapper.findUserIdByStudyId(studyId);
        boolean isAuthor = existingLog.getUserId().equals(userId);
        boolean isHost = leaderId != null && leaderId.equals(userId);

        if (!isAuthor && !isHost) {
            throw new BaseException(StudyErrorCode.STUDY_MEMBER_UNAUTHORIZED);
        }

        studyLogMapper.deleteLog(logId);
    }
}

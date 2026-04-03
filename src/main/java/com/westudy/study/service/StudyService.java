package com.westudy.study.service;

import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.service.AlarmService;
import com.westudy.global.exception.BaseException;
import com.westudy.global.util.RequireHelper;
import com.westudy.security.util.SecurityUtil;
import com.westudy.study.converter.StudyConverter;
import com.westudy.study.dto.*;
import com.westudy.study.enums.StudyErrorCode;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.enums.StudyStates;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StudyService {
    private final int PAGE_SIZE = 20;

    private final StudyMapper studyMapper;
    private final StudyConverter studyConverter;
    private final StudyParticipantMapper studyParticipantMapper;
    private final AlarmService alarmService;
    private final Object lock = new Object();

    public StudyService(StudyMapper studyMapper, StudyConverter studyConverter, StudyParticipantMapper studyParticipantMapper, AlarmService alarmService) {
        this.studyMapper = studyMapper;
        this.studyConverter = studyConverter;
        this.studyParticipantMapper = studyParticipantMapper;
        this.alarmService = alarmService;
    }


    @Transactional
    public void applicationStudy(long studyId) {
        long userId = SecurityUtil.getCurrentUserId();
        String nickname = SecurityUtil.getCurrentNickname();
        insertStudyParticipant(studyId);

        // 스터디 방장에게 알람 전송
        long leaderId = studyMapper.findUserIdByStudyId(studyId);
        StudyResponseDTO study = findByStudyId(studyId);
        alarmService.send(
                leaderId,
                userId,
                AlarmType.STUDY_APPLICATION,
                String.format("[%s]님이 '%s' 스터디에 신청하셨습니다.", nickname, study.getTitle()),
                "/page/study/detail?id=" + studyId
        );
    }


    public boolean approveAndCheckIfFull(long userId, long studyId){
        long currentUserId = SecurityUtil.getCurrentUserId();
        synchronized (lock){
            StudyResponseDTO study = findByStudyId(studyId);
            int maxMember = study.getMaxMember();
            int members = getStudyParticipantCount(studyId);

            if(maxMember > members){
                updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.APPROVED));
                
                // 신청자에게 승인 알람 전송
                alarmService.send(
                        userId,
                        currentUserId,
                        AlarmType.STUDY_APPROVE,
                        String.format("'%s' 스터디 신청이 승인되었습니다!", study.getTitle()),
                        "/page/post/detail?id=" + study.getPostId()
                );
            }else{
                throw new BaseException(StudyErrorCode.STUDY_FULL);
            }

            if(members+1 == maxMember){
                studyMapper.updateStudyState(StudyStates.CLOSED, studyId);
                return true;
            }
            return  false;
        }
    }

    public void requestReject(long userId, long studyId){
        long currentUserId = SecurityUtil.getCurrentUserId();
        updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.REJECTED));
        
        // 신청자에게 거절 알람 전송
        StudyResponseDTO study = findByStudyId(studyId);
        alarmService.send(
                userId,
                currentUserId,
                AlarmType.STUDY_REJECT,
                String.format("안타깝게도 '%s' 스터디 신청이 거절되었습니다.", study.getTitle()),
                null
        );
    }

    public void requestCancel(long studyId){
        long userId = SecurityUtil.getCurrentUserId();
        studyParticipantMapper.updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.CANCELLED));
    }

    public boolean canApprove(long studyId){
        int maxMember = findByStudyId(studyId).getMaxMember();
        int members = getStudyParticipantCount(studyId);

        return maxMember > members;
    }

    public List<StudyParticipanResponseDTO> getParticipantList(long studyId){
        isWriter(studyId);
        return findParticipantByStudyId(studyId);
    }

    public void isWriter(long studyId){
        long userId = SecurityUtil.getCurrentUserId();
        long studyUserId = studyMapper.findUserIdByStudyId(studyId);
        if(userId != studyUserId){
            throw new BaseException(StudyErrorCode.STUDY_NOT_WRITER);
        }
    }

    public void isParticipant(long userId){
        long id = SecurityUtil.getCurrentUserId();
        if(userId != id){
            throw new BaseException(StudyErrorCode.STUDY_NOT_PARTICIPANT);
        }
    }

    // create
    public void insertStudy(StudyInsertDTO studyInsertDTO){
        long userId = SecurityUtil.getCurrentUserId();
        if (studyInsertDTO.getDeadline() == null) {
            studyInsertDTO.setDeadline(java.time.LocalDateTime.now().plusDays(14)); // 미지정 시 14일 뒤로 설정
        }
        studyMapper.insertStudy(studyConverter.toStudy(studyInsertDTO, userId));
    }

    public void insertStudyParticipant(long studyId){
        long userId = SecurityUtil.getCurrentUserId();
        if(studyParticipantMapper.findByUserIdAndStudyId(userId, studyId) == 0){
            studyParticipantMapper.insertStudyParticipant(studyConverter.toStudyParticipant(studyId, userId));
        }else {
            throw new BaseException(StudyErrorCode.STUDY_ALREADY_APPLICATION);
        }

    }

    //read
    public List<StudyResponseDTO> getStudyList(int page){
        return RequireHelper.requireNonEmpty(
                studyMapper.findStudy(PAGE_SIZE, page), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );
    }

    public long getStudyCount(){
        return studyMapper.countStudy();
    }

    public StudyResponseDTO findByStudyId(long studyId){
        return RequireHelper.requireNonNull(
                studyMapper.findByStudyId(studyId), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );

    }

    public StudyResponseDTO findByPostId(long postId){
        return RequireHelper.requireNonNull(
                studyMapper.findByPostId(postId), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );
    }

    public List<StudyResponseDTO> findSearchStudy(String keyword, int page) {
        return RequireHelper.requireNonEmpty(
                studyMapper.findSearchStudy(keyword, page), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );
    }

    public long getSearchedStudyCount(String keyword) {
        return studyMapper.countSearchStudy(keyword);
    }

    public StudyResponseDTO getStudyDetailResponse(long id) {
        return RequireHelper.requireNonNull(
                studyMapper.findByPostId(id), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );
    }

    public List<StudyParticipanResponseDTO> findParticipantByStudyId(long studyId){
        return RequireHelper.requireNonEmpty(
                studyParticipantMapper.findByStudyId(studyId), new BaseException(StudyErrorCode.STUDY_EMPTY)
        );
    }

    public int getStudyParticipantCount(long studyId){
        return studyParticipantMapper.countStudyMember(studyId);
    }

    //update
    public void updateStudy(StudyUpdateDTO studyUpdateDTO){
        isWriter(studyUpdateDTO.getId());
        if(studyUpdateDTO.getState() == null){
            if(studyUpdateDTO.getMaxMember() > studyParticipantMapper.countStudyMember(studyUpdateDTO.getId())){
                studyUpdateDTO.setState(StudyStates.RECRUITING);
            }else {
                studyUpdateDTO.setState(StudyStates.CLOSED);
            }
        }
        studyMapper.updateStudy(studyUpdateDTO);
    }

    public void updateStudyParticipant(StudyParticipantUpdateDTO studyParticipantUpdateDTO){
        isParticipant(studyParticipantUpdateDTO.getUserId());
        studyParticipantMapper.updateStudyParticipant(studyParticipantUpdateDTO);
    }

    //delete
    public void deleteStudy(long studyId){
        isWriter(studyId);
        studyMapper.deleteStudy(studyId);
    }
}

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
import com.westudy.study.entity.Study;
import com.westudy.study.entity.StudyParticipant;
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


    @Transactional
    public boolean approveAndCheckIfFull(long userId, long studyId){
        long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 비관적 잠금 적용: 해당 스터디 로우에 락을 걸고 연산을 수행함
        studyMapper.findByIdForUpdate(studyId);
        
        StudyResponseDTO study = findByStudyId(studyId);
        int maxMember = study.getMaxMember();
        int members = getStudyParticipantCount(studyId);

        if(maxMember > members){
            studyParticipantMapper.updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.APPROVED));
            
            // 신청자에게 승인 알람 전송
            alarmService.send(
                    userId,
                    currentUserId,
                    AlarmType.STUDY_APPROVE,
                    String.format("'%s' 스터디 신청이 승인되었습니다!", study.getTitle()),
                    "/page/post/detail?id=" + study.getPostId()
            );
            
            // 승낙 후 가득 찼다면 상태 변경
            if(getStudyParticipantCount(studyId) == maxMember){
                studyMapper.updateStudyState(StudyStates.CLOSED, studyId);
                return true;
            }
        }else{
            throw new BaseException(StudyErrorCode.STUDY_FULL);
        }
        return false;
    }

    public void requestReject(long userId, long studyId){
        long currentUserId = SecurityUtil.getCurrentUserId();
        studyParticipantMapper.updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.REJECTED));
        
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

    @Transactional
    public void requestCancel(long studyId){
        long userId = SecurityUtil.getCurrentUserId();
        
        // 비관적 잠금 적용하여 취소 및 후속 처리 진행
        studyMapper.findByIdForUpdate(studyId);
        
        // 현재 상태 확인 (기존에 승인된 멤버였는지 확인하기 위함)
        StudyParticipant participant = studyParticipantMapper.findByUserId(userId);
        boolean wasApproved = (participant != null && participant.getStatus() == StudyParticipantStatus.APPROVED);

        studyParticipantMapper.updateStudyParticipant(new StudyParticipantUpdateDTO(userId, studyId, StudyParticipantStatus.CANCELLED));
        
        // 승인된 멤버가 취소하여 빈 자리가 생겼을 경우 후속 처리
        if (wasApproved) {
            notifyHostOfVacancy(studyId);
        }
    }

    private void notifyHostOfVacancy(long studyId) {
        StudyResponseDTO study = findByStudyId(studyId);
        long hostId = studyMapper.findUserIdByStudyId(studyId);
        
        // 모집 완료 상태였다면 자리가 생겼으므로 다시 RECRUITING으로 변경
        if (study.getState() == StudyStates.CLOSED) {
            studyMapper.updateStudyState(StudyStates.RECRUITING, studyId);
        }

        // 대기자가 있는지 확인 후 방장에게 알림 발송
        int waiterCount = studyParticipantMapper.countWaiters(studyId);
        if (waiterCount > 0) {
            alarmService.send(
                    hostId,
                    1L, // 시스템 알림 (ID 1 가정 또는 적절한 발신자 설정)
                    AlarmType.STUDY_APPLICATION,
                    String.format("'%s' 스터디에 빈 자리가 생겼습니다. 대기자 %d명을 확인하고 승인해 주세요!", study.getTitle(), waiterCount),
                    "/page/study/detail?id=" + studyId
            );
        }
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

    public List<MyPageStudyDTO> getParticipatingStudies(long userId) {
        return studyMapper.findParticipatingStudies(userId);
    }

    public List<MyPageStudyDTO> getOpenedStudies(long userId) {
        return studyMapper.findOpenedStudies(userId);
    }

    public List<MyPageStudyDTO> getBookmarkedStudies(long userId) {
        return studyMapper.findBookmarkedStudies(userId);
    }
}

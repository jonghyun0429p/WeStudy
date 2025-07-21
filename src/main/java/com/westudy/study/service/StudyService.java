package com.westudy.study.service;

import com.westudy.global.exception.BaseException;
import com.westudy.global.util.RequireHelper;
import com.westudy.security.util.SecurityUtil;
import com.westudy.study.controller.StudyContoller;
import com.westudy.study.converter.StudyConverter;
import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.enums.StudyErrorCode;
import com.westudy.study.enums.StudyStates;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StudyService {
    private final int PAGE_SIZE = 20;

    private final StudyMapper studyMapper;
    private final StudyConverter studyConverter;
    private final StudyParticipantMapper studyParticipantMapper;

    public StudyService(StudyMapper studyMapper, StudyConverter studyConverter, StudyParticipantMapper studyParticipantMapper) {
        this.studyMapper = studyMapper;
        this.studyConverter = studyConverter;
        this.studyParticipantMapper = studyParticipantMapper;
    }

    // create
    public void insertStudy(StudyInsertDTO studyInsertDTO){
        long userId = SecurityUtil.getCurrentUserId();
        studyMapper.insertStudy(studyConverter.toStudy(studyInsertDTO, userId));
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

    //update
    public void updateStudy(StudyUpdateDTO studyUpdateDTO){
        int memberCount = studyParticipantMapper.countStudyMember(studyUpdateDTO.getId());
        if(studyUpdateDTO.getMaxMember() > memberCount){
            studyUpdateDTO.setState(StudyStates.RECRUITING);
        }else{
            studyUpdateDTO.setState(StudyStates.CLOSED);
        }

        studyMapper.updateStudy(studyUpdateDTO);
    }

    //delete
    public void deleteStudy(long studyId){
        studyMapper.deleteStudy(studyId);
    }

}

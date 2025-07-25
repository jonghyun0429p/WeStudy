package com.westudy.study.converter;

import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyParticipantStatus;
import org.springframework.stereotype.Component;

@Component
public class StudyConverter {

    public Study toStudy(StudyInsertDTO studyInsertDTO, long userId){
        return Study.builder()
                .userId(userId)
                .title(studyInsertDTO.getTitle())
                .locaion(studyInsertDTO.getLocation())
                .maxMember(studyInsertDTO.getMaxMember())
                .state(studyInsertDTO.getState())
                .build();
    }
}
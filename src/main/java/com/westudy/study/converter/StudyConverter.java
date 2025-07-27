package com.westudy.study.converter;

import com.westudy.study.dto.StudyInsertDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.enums.StudyStates;
import org.springframework.stereotype.Component;

@Component
public class StudyConverter {

    public Study toStudy(StudyInsertDTO studyInsertDTO, long userId){
        return Study.builder()
                .userId(userId)
                .title(studyInsertDTO.getTitle())
                .location(studyInsertDTO.getLocation())
                .maxMember(studyInsertDTO.getMaxMember())
                .state(StudyStates.RECRUITING)
                .build();
    }
    public StudyParticipant toStudyParticipant(long studyId, long userId){
        return StudyParticipant.builder()
                .userId(userId)
                .studyId(studyId)
                .status(StudyParticipantStatus.WAITING)
                .build();
    }
}
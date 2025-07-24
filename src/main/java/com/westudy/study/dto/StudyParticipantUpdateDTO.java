package com.westudy.study.dto;

import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.enums.StudyStates;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyParticipantUpdateDTO {
    private long id;
    private long studyId;
    private StudyParticipantStatus status;

    public StudyParticipantUpdateDTO(long id, long studyId, StudyParticipantStatus studyParticipantStatus){
        this.id = id;
        this.studyId = studyId;
        this.status = studyParticipantStatus;
    }
}

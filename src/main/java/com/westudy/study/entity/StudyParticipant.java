package com.westudy.study.entity;

import com.westudy.study.enums.StudyParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyParticipant {
    private long id;
    private long studyId;
    private long userId;
    private LocalDateTime joinedAt;
    private StudyParticipantStatus status;
}

package com.westudy.study.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudyParticipanResponseDTO {
    private long id;
    private long studyId;
    private long userId;
    private String name;
    private LocalDateTime joinedAt;
}

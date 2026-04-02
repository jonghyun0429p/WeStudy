package com.westudy.study.dto;

import com.westudy.study.enums.StudyStates;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudyInsertDTO {
    private long post_id;
    private String title;
    private String location;
    private int maxMember;
    private LocalDateTime deadline;
    private StudyStates state;
}

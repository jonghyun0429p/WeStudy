package com.westudy.study.dto;

import com.westudy.study.enums.StudyStates;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyUpdateDTO {
    private long id;
    private String title;
    private String location;
    private int maxMember;
    private StudyStates state;
}

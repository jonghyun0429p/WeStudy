package com.westudy.study.dto;

import com.westudy.study.enums.StudyStates;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyResponseDTO {
    private long id;
    private long postId;
    private long userId;
    private String title;
    private String location;
    private int maxMember;
    private StudyStates state;
}

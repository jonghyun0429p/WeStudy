package com.westudy.study.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyInsertDTO {
    private long post_id;
    private String title;
    private String location;
    private int maxMember;
    private String state;
}

package com.westudy.study.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyEvent {
    private final Long studyId;
    private final String action; // "SAVE", "DELETE"
}

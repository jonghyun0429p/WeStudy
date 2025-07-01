package com.westudy.study.enums;

public enum StudyStates {
    RECRUITING("모집중"),
    CLOSED("모집 완료"),
    IN_PROGRESS("진행중"),
    FINISHED("종료");

    private final String state;


    StudyStates(String state) {
        this.state = state;
    }

    public String getState(){
        return state;
    }
}

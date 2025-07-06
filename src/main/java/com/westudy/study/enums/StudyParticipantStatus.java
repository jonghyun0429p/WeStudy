package com.westudy.study.enums;

public enum StudyParticipantStatus {
    WAITING("승인 대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨");

    private final String message;

    StudyParticipantStatus(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
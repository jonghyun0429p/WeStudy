package com.westudy.alarm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    COMMENT("새로운 댓글이 달렸습니다."),
    STUDY_APPLICATION("새물운 스터디 신청이 있습니다."),
    STUDY_APPROVE("스터디 신청이 승인되었습니다."),
    STUDY_REJECT("스터디 신청이 거절되었습니다."),
    CHAT_MESSAGE("새로운 채팅 메시지가 있습니다.");

    private final String defaultMessage;
}
